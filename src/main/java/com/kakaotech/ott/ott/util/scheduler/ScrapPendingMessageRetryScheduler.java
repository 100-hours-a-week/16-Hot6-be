package com.kakaotech.ott.ott.util.scheduler;

import com.kakaotech.ott.ott.global.config.StreamConfig;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.infrastructure.sycn.ScrapSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
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
public class ScrapPendingMessageRetryScheduler {

    private final StringRedisTemplate streamStringRedisTemplate;
    private final ScrapSyncService scrapSyncService;
    private final List<StreamConfig> configs;

    public ScrapPendingMessageRetryScheduler(
            @Qualifier("streamStringRedisTemplate") StringRedisTemplate streamStringRedisTemplate,
            ScrapSyncService scrapSyncService,
            List<StreamConfig> configs
    ) {
        this.streamStringRedisTemplate = streamStringRedisTemplate;
        this.scrapSyncService = scrapSyncService;
        this.configs = configs;
    }

    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void retryAllPending() {
        for (StreamConfig cfg : configs){
            retryPendingFor(cfg);
        }

    }

    private void retryPendingFor(StreamConfig cfg) {
        var pendingPost = streamStringRedisTemplate.opsForStream()
                .pending(cfg.getStreamKey(), cfg.getGroup(), Range.unbounded(), 100);

        for (PendingMessage msg : pendingPost) {
            if (msg.getElapsedTimeSinceLastDelivery().toMillis() < 30_000)
                continue;

            @SuppressWarnings("unchecked")
            List<MapRecord<String, String, String>> claimed =
                    (List<MapRecord<String, String, String>>) (List<?>)
                            streamStringRedisTemplate.opsForStream()
                                    .claim(
                                            cfg.getStreamKey(),
                                            cfg.getGroup(),
                                            cfg.getRetryConsumer(),
                                            Duration.ofMillis(1),
                                            msg.getId()
                                    );

            if (claimed == null || claimed.isEmpty()) {
                continue;
            }

            List<Object[]> params = claimed.stream()
                    .map(r -> {
                        Map<String, String> v = r.getValue();
                        long userId = Long.parseLong(v.get("userId"));
                        String type = v.get("type");
                        long targetId = Long.parseLong(v.get("targetId"));
                        boolean scrap = "scrap".equals(v.get("action"));
                        String eventId = r.getId().toString();
                        return new Object[]{userId, type, targetId, scrap, eventId};
                    })
                    .collect(Collectors.toList());

            Map<String, Long> deltas = claimed.stream()
                    .collect(Collectors.groupingBy(
                            r -> r.getValue().get("type")
                                    + ":"
                                    + r.getValue().get("targetId"),
                            Collectors.summingLong(r ->
                                    "scrap".equals(r.getValue().get("action")) ? 1L : -1L)
                    ));

            try {
                scrapSyncService.syncBatchWithVersion(params, deltas);

                RecordId[] ids = claimed.stream()
                        .map(MapRecord::getId)
                        .toArray(RecordId[]::new);
                streamStringRedisTemplate.opsForStream().acknowledge(cfg.getStreamKey(), cfg.getGroup(), ids);
                streamStringRedisTemplate.opsForStream().delete(cfg.getStreamKey(), ids);
            } catch (Exception e) {
                log.error("[PendingRetry - Scrap] 메시지 처리 실패: {}", msg.getId(), e);
            }
        }
    }

}
