package com.kakaotech.ott.ott.like.infrastructure.sync;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeEventPublisher {

    private static final String LIKE_STREAM_KEY = "like:stream:events";
    private final RedisTemplate<String, LikeEvent> redisTemplate;

    public void publish(LikeEvent event) {
        ObjectRecord<String, LikeEvent> record = StreamRecords
                .objectBacked(event)
                .withStreamKey(LIKE_STREAM_KEY);

        redisTemplate.opsForStream().add(record);
    }
}