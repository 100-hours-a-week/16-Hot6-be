package com.kakaotech.ott.ott.aiImage.infrastructure.stream;

import com.kakaotech.ott.ott.util.scheduler.AiImageRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiImageStreamMonitor {
    private static final Duration IDLE_THRESHOLD = Duration.ofMinutes(1);
    private static final long PAGE_SIZE = 50;

    private final StringRedisTemplate stringRedisTemplate;
    private final AiImageResultConsumer aiImageResultConsumer;

    private String getConsumerName() {
        return "monitor-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // 주기적으로 보류 중인 메시지 재처리
    @Scheduled(fixedRate = 300000) // 5분마다
    @SchedulerLock(name = "ai-image-stalled-processor", lockAtMostFor = "PT2M")
    public void reclaimStalledMessages() {
        try {
            reclaimStalled(AiImageRedisKey.COMPLETED_IMAGES_STREAM, AiImageRedisKey.TO_BE_GROUP);
        } catch (Exception e) {
            log.error("Failed to process stalled messages", e);
        }
    }

    private void reclaimStalled(String streamKey, String groupName) {
        try {
            // 1. 보류 중인 메시지 요약 정보 조회
            PendingMessagesSummary summary = stringRedisTemplate.opsForStream()
                    .pending(streamKey, groupName);

            if (summary == null || summary.getTotalPendingMessages() == 0) {
                log.debug("No pending messages to process in stream: {}", streamKey);
                return;
            }

            log.warn("Found {} pending messages in stream: {}, group: {}",
                    summary.getTotalPendingMessages(), streamKey, groupName);

            // 2. 상세 보류 메시지 목록 조회
            PendingMessages pendingMessages = stringRedisTemplate.opsForStream()
                    .pending(streamKey, groupName, Range.unbounded(), PAGE_SIZE);

            // 3. 1분 이상 대기 중인 메시지들 처리
            List<RecordId> stalledIds = new ArrayList<>();
            for (PendingMessage msg : pendingMessages) {
                if (msg.getElapsedTimeSinceLastDelivery().toMillis() > IDLE_THRESHOLD.toMillis()) {
                    stalledIds.add(RecordId.of(msg.getIdAsString()));
                }
            }

            if (stalledIds.isEmpty()) return;

            // 재할당 및 처리
            claimAndProcess(streamKey, groupName, stalledIds);

        } catch (Exception e) {
            log.error("Failed to monitor pending messages for stream: {}", streamKey, e);
        }
    }

    private void claimAndProcess(String streamKey, String groupName, List<RecordId> messageIds) {
        String newConsumer = "stalled-processor-" + System.currentTimeMillis();

        try {
            List<MapRecord<String, Object, Object>> claimed = stringRedisTemplate.opsForStream()
                    .claim(streamKey, groupName, newConsumer, IDLE_THRESHOLD,
                            messageIds.toArray(new RecordId[0]));

            log.info("Processing {} claimed messages", claimed.size());

            // 기존 Consumer 로직 재사용
            for (MapRecord<String, Object, Object> message : claimed) {
                try {
                    MapRecord<String, String, String> stringMessage = convertToStringRecord(message);
                    aiImageResultConsumer.handleCompletedImageResult(stringMessage);
                    log.info("Successfully reprocessed message: {}", message.getId());
                } catch (Exception e) {
                    log.error("Failed to reprocess message: {}", message.getId(), e);
                    // 실패해도 다음번에 다시 시도할 수 있도록 ACK 안함
                }
            }
        } catch (Exception e) {
            log.error("Failed to claim messages", e);
        }
    }

    // 스트림 크기 관리 (매일 새벽 2시)
//    @Scheduled(cron = "0 0 2 * * *")
//    public void trimStreams() {
//        // 최대 10,000개 메시지만 유지
//        aiImageEventPublisher.trimStream(AiImageRedisKey.ORIGINAL_IMAGES_STREAM, 10000);
//        aiImageEventPublisher.trimStream(AiImageRedisKey.COMPLETED_IMAGES_STREAM, 10000);
//
//        log.info("Daily stream trimming completed");
//    }

    // 스트림 상태 로깅 (매 10분마다)
    @Scheduled(fixedRate = 600000) // 10분마다
    public void logStreamStatus() {
        try {
            var originalStreamInfo = stringRedisTemplate.opsForStream()
                    .info(AiImageRedisKey.ORIGINAL_IMAGES_STREAM);
            var completedStreamInfo = stringRedisTemplate.opsForStream()
                    .info(AiImageRedisKey.COMPLETED_IMAGES_STREAM);

            log.info("Stream Status - Original: {} messages, Completed: {} messages",
                    originalStreamInfo.streamLength(), completedStreamInfo.streamLength());

        } catch (Exception e) {
            log.debug("Failed to get stream status (streams may not exist yet): {}", e.getMessage());
        }
    }

    // 타입 변환 헬퍼 메서드
    private MapRecord<String, String, String> convertToStringRecord(MapRecord<String, Object, Object> source) {
        Map<String, String> stringMap = new HashMap<>();

        // Object를 String으로 안전하게 변환
        for (Map.Entry<Object, Object> entry : source.getValue().entrySet()) {
            String key = entry.getKey() != null ? entry.getKey().toString() : "";
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            stringMap.put(key, value);
        }

        return StreamRecords.newRecord()
                .ofMap(stringMap)
                .withStreamKey(source.getStream())
                .withId(source.getId());
    }
}
