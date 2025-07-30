package com.kakaotech.ott.ott.aiImage.infrastructure.stream;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImageConcept;
import com.kakaotech.ott.ott.util.scheduler.AiImageRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiImageEventPublisher {
    private final StringRedisTemplate stringRedisTemplate;

    // AI 서버로 이미지 처리 요청 이벤트 발행
    public String publishImageProcessingRequest(String initialImageUrl, AiImageConcept concept) {
        try {
            Map<String, String> messageData = Map.of(
                    "initial_image_url", initialImageUrl,
                    "concept", String.valueOf(concept)
            );

            MapRecord<String, String, String> record = StreamRecords.newRecord()
                    .ofMap(messageData)
                    .withStreamKey(AiImageRedisKey.ORIGINAL_IMAGES_STREAM);

            RecordId recordId = stringRedisTemplate.opsForStream().add(record);

            log.info("Published image processing request to stream '{}': recordId={}",
                    AiImageRedisKey.ORIGINAL_IMAGES_STREAM, recordId);

            return recordId.getValue();

        } catch (Exception e) {
            log.error("Failed to publish image processing request: ", e);
            throw new RuntimeException("이미지 처리 요청 발행 실패", e);
        }
    }

    // 스트림 크기 관리를 위한 트리밍
    public void trimStream(String streamKey, long maxLength) {
        try {
            stringRedisTemplate.opsForStream().trim(streamKey, maxLength, true);
            log.debug("Trimmed stream '{}' to max length: {}", streamKey, maxLength);
        } catch (Exception e) {
            log.error("Failed to trim stream '{}'", streamKey, e);
        }
    }
}
