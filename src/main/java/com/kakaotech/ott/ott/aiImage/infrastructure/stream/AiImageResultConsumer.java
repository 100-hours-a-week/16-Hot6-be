package com.kakaotech.ott.ott.aiImage.infrastructure.stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaotech.ott.ott.aiImage.application.service.AiImageService;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;
import com.kakaotech.ott.ott.recommendProduct.application.service.ProductDomainService;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.request.ProductDetailRequestDto;
import com.kakaotech.ott.ott.util.scheduler.AiImageRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiImageResultConsumer {
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final AiImageService aiImageService;
    private final ProductDomainService productDomainService;

    // AI 서버에서 전송된 이미지 처리 결과를 처리
    public void handleCompletedImageResult(MapRecord<String, String, String> record) {
        String streamKey = AiImageRedisKey.COMPLETED_IMAGES_STREAM;
        String groupName = AiImageRedisKey.TO_BE_GROUP;

        try {
            // 1. 메시지 데이터 파싱
            Map<String, String> messageData = record.getValue();
            AiImageAndProductRequestDto requestDto = parseMessageToRequestDto(messageData);

            // 2. 기존 비즈니스 로직 실행 (Controller의 receiveResult와 동일)
            AiImage aiImage = aiImageService.insertAiImage(requestDto);
            productDomainService.createdProduct(requestDto, aiImage, aiImage.getUserId());

            // 3. 성공적으로 처리했음을 Redis에 알림 (ACK)
            acknowledgeMessage(streamKey, groupName, record.getId());

            log.info("Successfully processed AI image result: aiImageId={}, recordId={}",
                    aiImage.getId(), record.getId());

        } catch (ParseException e) {
            // 파싱 에러는 재시도 불가능 - 즉시 ACK
            log.error("Failed to parse AI image result message, skipping: recordId={}", record.getId(), e);
            acknowledgeMessage(streamKey, groupName, record.getId());

        } catch (Exception e) {
            // 비즈니스 로직 에러 - 재시도 가능 (ACK 하지 않음)
            log.error("Failed to process AI image result, will retry: recordId={}", record.getId(), e);
        }
    }

    private AiImageAndProductRequestDto parseMessageToRequestDto(Map<String, String> messageData) throws ParseException {
        try {
            // initialImageUrl 설정
            String initialImageUrl = messageData.get("initial_image_url");
            if (initialImageUrl == null) {
                throw new ParseException("initialImageUrl is required");
            }

            // processedImageUrl 설정 (nullable)
            String processedImageUrl = messageData.get("processed_image_url");

            // products JSON 문자열을 객체 리스트로 변환
            String productsJson = messageData.get("products");
            List<ProductDetailRequestDto> products = Collections.emptyList();

            if (productsJson != null && !productsJson.isEmpty()) {
                products = objectMapper.readValue(
                        productsJson,
                        new TypeReference<List<ProductDetailRequestDto>>() {}
                );
            }

            return AiImageAndProductRequestDto.builder()
                    .initialImageUrl(initialImageUrl)
                    .processedImageUrl(processedImageUrl)
                    .products(products)
                    .build();

        } catch (Exception e) {
            throw new ParseException("Failed to parse message data: " + messageData, e);
        }
    }

    private void acknowledgeMessage(String streamKey, String groupName, RecordId recordId) {
        try {
            stringRedisTemplate.opsForStream().acknowledge(streamKey, groupName, recordId);
            stringRedisTemplate.opsForStream().delete(streamKey, recordId);
            log.debug("Acknowledged and deleted message: recordId={}", recordId);
        } catch (Exception e) {
            log.error("Failed to acknowledge message: recordId={}", recordId, e);
        }
    }

    // 커스텀 파싱 예외
    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }

        public ParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
