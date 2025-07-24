package com.kakaotech.ott.ott.global.config;

import com.kakaotech.ott.ott.like.infrastructure.sync.LikeSyncService;
import com.kakaotech.ott.ott.scrap.infrastructure.sycn.ScrapSyncService;
import com.kakaotech.ott.ott.util.scheduler.LikeRedisKey;
import com.kakaotech.ott.ott.util.scheduler.ScrapRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RedisStreamConsumerConfig {

    private static final String LIKE_STREAM_KEY = LikeRedisKey.LIKE_STREAM_KEY;

    private static final String LIKE_GROUP = "likeGroup";

    private static final Duration POLL_TIMEOUT  = Duration.ofSeconds(1);
    private static final int BATCH_SIZE         = 10;

    private final RedisConnectionFactory connectionFactory;
    private final StringRedisTemplate     redisTemplate;
    private final LikeSyncService         likeSyncService;
    private final ScrapSyncService        scrapSyncService;

    @Bean(destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String,String,String>>
    likeListenerContainer() {
        try {
            redisTemplate.opsForStream().createGroup(LIKE_STREAM_KEY, LIKE_GROUP);
        } catch (Exception ignored) {}

        StreamMessageListenerContainerOptions<String, MapRecord<String,String,String>> opts =
                StreamMessageListenerContainerOptions
                        .<String, MapRecord<String,String,String>>builder()
                        .pollTimeout(POLL_TIMEOUT)
                        .batchSize(BATCH_SIZE)
                        .build();

        var container = StreamMessageListenerContainer
                .create(connectionFactory, opts);

        String consumerName = "likeConsumer-" + UUID.randomUUID().toString();
        container.receive(
                Consumer.from(LIKE_GROUP, consumerName),
                StreamOffset.create(LIKE_STREAM_KEY, ReadOffset.lastConsumed()),
                this::handleMapRecord
        );

        container.start();
        log.info("▶ Stream listener started: group='{}', consumer='{}'", LIKE_GROUP, consumerName);
        return container;
    }

    private void handleMapRecord(MapRecord<String,String,String> record) {
        Map<String,String> m = record.getValue();
        String sUser   = m.get("userId");
        String sPost   = m.get("postId");
        String action  = m.get("action");

        if (sUser == null || sPost == null || action == null) {
            log.warn("▶ Skip invalid record: {}", record);
            ackAndDelete(LIKE_STREAM_KEY, LIKE_GROUP, record.getId());
            return;
        }

        long userId, postId;
        try {
            userId = Long.parseLong(sUser);
            postId = Long.parseLong(sPost);
        } catch (NumberFormatException ex) {
            log.warn("▶ Skip malformed IDs: {}", record, ex);
            ackAndDelete(LIKE_STREAM_KEY, LIKE_GROUP, record.getId());
            return;
        }

        boolean isLike = "like".equals(action);
        String eventId = record.getId().toString();

        List<Object[]> upsertParams = List.<Object[]>of(
                new Object[]{ userId, postId, isLike, isLike, eventId }
        );
        Map<Long,Long> deltas = Map.of(postId, isLike ? 1L : -1L);

        try {
            likeSyncService.syncBatchWithVersion(upsertParams, deltas);
            ackAndDelete(LIKE_STREAM_KEY, LIKE_GROUP, record.getId());
        } catch (Exception ex) {
            log.error("▶ syncBatchWithVersion failed for {}, will retry later", record.getId(), ex);
        }
    }

    @Bean(destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>>
    scrapPostListenerContainer() {
        return createScrapContainer(
                ScrapRedisKey.SCRAP_STREAM_KEY_POST,
                "scrapPostGroup"
        );
    }

    @Bean(destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>>
    scrapProductListenerContainer() {
        return createScrapContainer(
                ScrapRedisKey.SCRAP_STREAM_KEY_PRODUCT,
                "scrapProductGroup"
        );
    }

    @Bean(destroyMethod = "stop")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>>
    scrapServiceProductListenerContainer() {
        return createScrapContainer(
                ScrapRedisKey.SCRAP_STREAM_KEY_SERVICE_PRODUCT,
                "scrapServiceProductGroup"
        );
    }

    private StreamMessageListenerContainer<String, MapRecord<String, String, String>> createScrapContainer(
            String streamKey, String group) {
        try {
            redisTemplate.opsForStream().createGroup(streamKey, group);
        } catch (Exception ignored) {}

        var opts = StreamMessageListenerContainerOptions
                .<String, MapRecord<String, String, String>>builder()
                .pollTimeout(POLL_TIMEOUT)
                .batchSize(BATCH_SIZE)
                .build();

        var container = StreamMessageListenerContainer
                .create(connectionFactory, opts);

        String consumer = group + "-" + UUID.randomUUID();
        container.receive(
                Consumer.from(group, consumer),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                record -> handleScrapEvent(record, streamKey, group)
        );
        container.start();
        log.info("▶ Scrap listener started: stream='{}', group='{}', consumer='{}'",
                streamKey, group, consumer);
        return container;
    }

    private void handleScrapEvent(MapRecord<String, String, String> record, String streamKey, String group) {
        Map<String, String> map = record.getValue();
        String sUser    = map.get("userId");
        String sTarget  = map.get("targetId");
        String type     = map.get("type");
        String action   = map.get("action");

        if (sUser == null || sTarget == null || type == null || action == null) {
            log.warn("▶ Skip invalid scrap record: {}", record);
            ackAndDelete(streamKey, group, record.getId());
            return;
        }

        long userId, targetId;
        try {
            userId = Long.parseLong(sUser);
            targetId = Long.parseLong(sTarget);
        } catch (NumberFormatException ex) {
            log.warn("▶ Skip malformed IDs in scrap record: {}", record, ex);
            ackAndDelete(streamKey, group, record.getId());
            return;
        }

        boolean isScrap = "scrap".equals(action);
        String eventId = record.getId().toString();

        List<Object[]> upsertParams =
                List.<Object[]>of(new Object[]{userId, type, targetId, isScrap, eventId});
        Map<String, Long> deltas = Map.of(type + ":" + targetId, isScrap ? 1L : -1L);

        try {
            scrapSyncService.syncBatchWithVersion(upsertParams, deltas);
            ackAndDelete(streamKey, group, record.getId());
        } catch (Exception ex) {
            log.error("▶ Like sync failed for {}, will retry later", record.getId(), ex);
        }
    }
    private void ackAndDelete(String streamKey, String group, RecordId id) {
        redisTemplate.opsForStream().acknowledge(streamKey, group, id);
        redisTemplate.opsForStream().delete(streamKey, id);
    }
}
