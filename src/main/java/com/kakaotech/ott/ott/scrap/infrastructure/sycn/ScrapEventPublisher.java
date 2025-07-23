package com.kakaotech.ott.ott.scrap.infrastructure.sycn;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapEventPublisher {

    private static final String SCRAP_STREAM_KEY_POST = "scrap:stream:post:events";
    private static final String SCRAP_STREAM_KEY_PRODUCT = "scrap:stream:product:events";
    private static final String SCRAP_STREAM_KEY_SERVICE_PRODUCT = "scrap:stream:service-product:events";

    private final RedisTemplate<String, ScrapEvent> redisTemplate;

    public void publish(ScrapEvent event) {
        String streamKey = switch (event.getType()) {
            case POST               -> SCRAP_STREAM_KEY_POST;
            case PRODUCT            -> SCRAP_STREAM_KEY_PRODUCT;
            case SERVICE_PRODUCT    -> SCRAP_STREAM_KEY_SERVICE_PRODUCT;
        };

        ObjectRecord<String, ScrapEvent> record = StreamRecords
                .objectBacked(event)
                .withStreamKey(streamKey);

        redisTemplate.opsForStream().add(record);
    }
}
