package com.kakaotech.ott.ott.global.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kakaotech.ott.ott.global.cache.CustomCacheErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
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
public class RedisConfig implements CachingConfigurer {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    // 캐시 키 상수 정의
    public static final String POPULAR_SETUPS_CACHE = "main:popular:posts";
    public static final String RECOMMEND_ITEMS_CACHE = "main:recommend:products";
    public static final String TODAY_PROMOTION_CACHE = "main:today:promotions";

    // feat, dev 환경을 위한 Redis Standalone 설정
    @Bean(destroyMethod = "shutdown")
    @Profile({"feat", "dev"})
    public RedissonClient redissonClient(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port,
            @Value("${spring.data.redis.password}") String password
    ) {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);
        return Redisson.create(config);
    }

    // prod 환경을 위한 Redis Sentinel 설정
    @Bean(destroyMethod = "shutdown")
    @Profile("prod")
    public RedissonClient redissonClientProd(
            @Value("${spring.data.redis.sentinel.master}") String masterName,
            @Value("${spring.data.redis.sentinel.nodes}") String nodes,
            @Value("${spring.data.redis.password}") String masterPassword,
            @Value("${spring.data.redis.sentinel.password}") String sentinelPassword
    ) {
        Config config = new Config();
        String[] sentinelNodes = Arrays.stream(nodes.split(","))
                .map(node -> "redis://" + node.trim())
                .toArray(String[]::new);

        config.useSentinelServers()
                .setMasterName(masterName)
                .addSentinelAddress(sentinelNodes)
                .setPassword(masterPassword)
                .setSentinelPassword(sentinelPassword);
        return Redisson.create(config);
    }

    // Redisson 기반 RedisConnectionFactory 생성
    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedissonClient redissonClient) {
        return new RedissonConnectionFactory(redissonClient);
    }

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
                    defaultCacheConfig().entryTtl(baseTtl));

            // 추천 상품 캐시 (자정까지)
            cacheConfigurations.put(RECOMMEND_ITEMS_CACHE,
                    defaultCacheConfig().entryTtl(baseTtl));

            // 특가 상품 캐시 (오후 1시까지)
            cacheConfigurations.put(TODAY_PROMOTION_CACHE,
                    defaultCacheConfig().entryTtl(baseTtl));

            builder.withInitialCacheConfigurations(cacheConfigurations);

        };
    }

    @Override
    @Bean
    public CacheErrorHandler errorHandler() {
        return new CustomCacheErrorHandler();
    }

    // === Helper Method ===
    // 시간 계산 헬퍼 메서드들
    private Duration getCacheTtl() {
        if ("prod".equals(activeProfile)) {
            return Duration.ofHours(25);
        }
        return Duration.ofHours(1);
    }
}
