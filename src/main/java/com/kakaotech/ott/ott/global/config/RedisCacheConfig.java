package com.kakaotech.ott.ott.global.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig {

    private final RedisProperties redisProperties;

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

    // Redis Sentinel 연결 팩토리 생성
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        try {
            // Sentinel 설정 객체 생성
            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();

            // 1. Master 이름 설정 (논리적 이름)
            String masterName = redisProperties.getSentinel().getMaster();
            sentinelConfig.setMaster(masterName);

            // 2. Sentinel 노드들 추가 (실제 Sentinel 서버들)
            Set<String> sentinelNodes = redisProperties.getSentinel().getNodes();
            for (String node : sentinelNodes) {
                String[] hostPort = node.split(":");
                if (hostPort.length == 2) {
                    sentinelConfig.sentinel(hostPort[0], Integer.parseInt(hostPort[1]));
                }
            }

            // 3. 패스워드 설정
            if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
                sentinelConfig.setPassword(redisProperties.getPassword());
            }

            // 4. Sentinel 패스워드 설정 (별도 패스워드가 있는 경우)
            if (redisProperties.getSentinel().getPassword() != null) {
                sentinelConfig.setSentinelPassword(redisProperties.getSentinel().getPassword());
            }

            // 5. 데이터베이스 번호 설정 (기본 0)
            sentinelConfig.setDatabase(redisProperties.getDatabase());

            // 6. Lettuce 연결 팩토리 생성
            LettuceConnectionFactory factory = new LettuceConnectionFactory(sentinelConfig);
            factory.setTimeout(Duration.ofMillis(redisProperties.getTimeout().toMillis()));

            log.info("✅ Redis Sentinel 연결 팩토리 생성 완료");
            return factory;

        } catch (Exception e) {
            log.error("❌ Redis Sentinel 연결 팩토리 생성 실패", e);
            throw new RuntimeException("Redis Sentinel 설정 오류", e);
        }
    }


    // Redis 직렬화 설정
    @Bean
    public GenericJackson2JsonRedisSerializer jsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(cacheObjectMapper());
    }

    // RedisTemplate 설정 - 일반적인 Redis 작업용
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key는 String, Value는 JSON으로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonRedisSerializer());

        template.afterPropertiesSet();
        log.info("RedisTemplate 설정 완료");
        return template;
    }
}
