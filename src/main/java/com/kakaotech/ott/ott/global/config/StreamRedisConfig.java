package com.kakaotech.ott.ott.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@RequiredArgsConstructor
public class StreamRedisConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    public StringRedisTemplate streamStringRedisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}
