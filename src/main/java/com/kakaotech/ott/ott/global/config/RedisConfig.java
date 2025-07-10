package com.kakaotech.ott.ott.global.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    // 캐시 키 상수 정의
    public static final String POPULAR_SETUPS_CACHE = "popularSetups";
    public static final String RECOMMEND_ITEMS_CACHE = "recommendItems";
    public static final String TODAY_PROMOTION_CACHE = "todayPromotion";
    public static final String HOME_DATA_CACHE = "homeData";

    // ObjectMapper 설정 - JSON 직렬화/역직렬화 최적화
    @Bean
    public ObjectMapper cacheObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Java 8 시간 API 지원
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // null 값 제외
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }

    // Redis Json 직렬화 설정
    @Bean
    public GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(cacheObjectMapper());
    }

    // RedisTemplate 설정 - <String, JSON>
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key는 String, Value는 JSON으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonRedisSerializer());
        template.setHashValueSerializer(jsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    // 숫자 연산(조회수, 좋아요, 통계)
    @Bean(name = "counterRedisTemplate")
    public RedisTemplate<String, Long> counterRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    // 기본 캐시 설정
    private RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jsonRedisSerializer()))
                .disableCachingNullValues();
    }

    // 캐시별 개별 설정 커스터마이저
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> {
            Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

            Duration baseTtl = getCacheTtl();

            // 인기 게시글 캐시 (자정까지)
            cacheConfigurations.put(POPULAR_SETUPS_CACHE,
                    defaultCacheConfig().entryTtl(getTimeUntilMidnight(baseTtl)));

            // 추천 상품 캐시 (자정까지)
            cacheConfigurations.put(RECOMMEND_ITEMS_CACHE,
                    defaultCacheConfig().entryTtl(getTimeUntilMidnight(baseTtl)));

            // 특가 상품 캐시 (오후 1시까지)
            cacheConfigurations.put(TODAY_PROMOTION_CACHE,
                    defaultCacheConfig().entryTtl(getTimeUntil13PM(baseTtl)));

            // 홈 데이터 통합 캐시 (가장 짧은 TTL)
            cacheConfigurations.put(HOME_DATA_CACHE,
                    defaultCacheConfig().entryTtl(getShorterTtl(baseTtl)));

            builder.withInitialCacheConfigurations(cacheConfigurations);

        };
    }

    // === Helper Method ===
    // 시간 계산 헬퍼 메서드들
    private Duration getCacheTtl() {
        if ("prod".equals(activeProfile)) {
            return Duration.ofHours(25);
        }
        return Duration.ofHours(1);
    }

    private Duration getTimeUntilMidnight(Duration fallback) {
        if ("dev".equals(activeProfile)) {
            return fallback;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return Duration.between(now, midnight);
    }

    private Duration getTimeUntil13PM(Duration fallback) {
        if ("dev".equals(activeProfile)) {
            return fallback;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime target = now.toLocalDate().atTime(13, 0);

        if (now.isAfter(target)) {
            target = target.plusDays(1);
        }

        return Duration.between(now, target);
    }

    private Duration getShorterTtl(Duration fallback) {
        if ("dev".equals(activeProfile)) {
            return fallback;
        }

        Duration untilMidnight = getTimeUntilMidnight(fallback);
        Duration until13PM = getTimeUntil13PM(fallback);

        return untilMidnight.compareTo(until13PM) < 0 ? untilMidnight : until13PM;
    }
}
