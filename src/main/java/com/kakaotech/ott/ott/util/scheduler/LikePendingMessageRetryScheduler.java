package com.kakaotech.ott.ott.util.scheduler;

import com.kakaotech.ott.ott.like.infrastructure.sync.LikeSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class LikePendingMessageRetryScheduler {

    private final String STREAM         = LikeRedisKey.LIKE_STREAM_KEY;
    private final String GROUP          = "likeGroup";
    private final String RETRY_CONSUMER = "retryConsumer";

    private final StringRedisTemplate streamStringRedisTemplate;
    private final LikeSyncService     likeSyncService;

    public LikePendingMessageRetryScheduler(
            @Qualifier("streamStringRedisTemplate") StringRedisTemplate streamStringRedisTemplate,
            LikeSyncService likeSyncService
    ) {
        this.streamStringRedisTemplate = streamStringRedisTemplate;
        this.likeSyncService = likeSyncService;
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void retryPending() {
        PendingMessages pending = streamStringRedisTemplate.opsForStream()
                .pending(STREAM, GROUP, Range.unbounded(), 100);

        for (PendingMessage msg : pending) {
            if (msg.getElapsedTimeSinceLastDelivery().toMillis() < 30_000)
                continue;

            @SuppressWarnings("unchecked")
            List<MapRecord<String, String, String>> claimed =
                    (List<MapRecord<String, String, String>>) (List<?>)
                            streamStringRedisTemplate.opsForStream()
                                    .claim(
                                            STREAM,
                                            GROUP,
                                            RETRY_CONSUMER,
                                            Duration.ofMillis(1),   // ← 여기서 long + TimeUnit 대신 Duration 사용
                                            msg.getId()             // RecordId…
                                    );


            if (claimed == null || claimed.isEmpty()) {
                log.warn("⚠️ 메시지 claim 실패 또는 없음: {}", msg.getId());
                continue;
            }

            // 1) upsert 파라미터 생성
            List<Object[]> params = claimed.stream()
                    .map(r -> {
                        Map<String, String> v = r.getValue();
                        long userId = Long.parseLong(v.get("userId"));
                        long postId = Long.parseLong(v.get("postId"));
                        boolean like = "like".equals(v.get("action"));
                        String eventId = r.getId().toString();
                        return new Object[]{userId, postId, like, like, eventId};
                    })
                    .collect(Collectors.toList());

            // 2) 델타 계산
            Map<Long, Long> deltas = claimed.stream()
                    .collect(Collectors.groupingBy(
                            r -> Long.parseLong(r.getValue().get("postId")),
                            Collectors.summingLong(r -> "like".equals(r.getValue().get("action")) ? 1L : -1L)
                    ));

            try {
                likeSyncService.syncBatchWithVersion(params, deltas);

                RecordId[] ids = claimed.stream()
                        .map(MapRecord::getId)
                        .toArray(RecordId[]::new);
                streamStringRedisTemplate.opsForStream().acknowledge(STREAM, GROUP, ids);
                streamStringRedisTemplate.opsForStream().delete(STREAM, ids);
            } catch (Exception e) {
                log.error("[PendingRetry] 메시지 처리 실패: {}", msg.getId(), e);
            }
        }
    }
}
