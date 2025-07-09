package com.kakaotech.ott.ott.global.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisCacheConfig {

    @Value("${spring.profiles.active}")
    private String activeProfile;

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
            List<String> sentinelNodes = redisProperties.getSentinel().getNodes();
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
            factory.setTimeout(redisProperties.getTimeout().toMillis());

            return factory;

        } catch (Exception e) {
            throw new RuntimeException("Redis Sentinel 설정 오류", e);
        }
    }


    // Redis 직렬화 설정
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
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    // 기본 캐시 설정
    private RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
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

    // Helper Method
    private String getMasterName() {
        String masterName = System.getenv("REDIS_MASTER_NAME");
        if (masterName == null || masterName.trim().isEmpty()) {
            masterName = "mymaster";
            log.info("🔄 기본 Master 이름 사용: {}", masterName);
        }
        return masterName;
    }

    private List<String> getSentinelNodesList() {
        String nodesEnv = System.getenv("REDIS_SENTINEL_NODES");
        if (nodesEnv == null || nodesEnv.trim().isEmpty()) {
            log.warn("⚠️ REDIS_SENTINEL_NODES 환경변수가 설정되지 않음");
            return new ArrayList<>();
        }

        List<String> nodes = new ArrayList<>();
        String[] nodeArray = nodesEnv.split(",");

        for (String node : nodeArray) {
            String trimmedNode = node.trim();
            if (!trimmedNode.isEmpty()) {
                nodes.add(trimmedNode);
            }
        }

        log.info("📋 Sentinel 노드 목록: {}", nodes);
        return nodes;
    }

    private void addSentinelNodeSafely(RedisSentinelConfiguration config, String node) {
        if (node == null || node.trim().isEmpty()) {
            return;
        }

        String trimmedNode = node.trim();
        String[] hostPort = trimmedNode.split(":");

        if (hostPort.length != 2) {
            log.warn("⚠️ 잘못된 Sentinel 노드 형식: {} (올바른 형식: host:port)", trimmedNode);
            return;
        }

        try {
            String host = hostPort[0].trim();
            int port = Integer.parseInt(hostPort[1].trim());

            if (host.isEmpty() || port <= 0 || port > 65535) {
                log.warn("⚠️ 잘못된 호스트 또는 포트: {}:{}", host, port);
                return;
            }

            config.sentinel(host, port);
            log.info("➕ Sentinel 노드 추가: {}:{}", host, port);

        } catch (NumberFormatException e) {
            log.warn("⚠️ 잘못된 포트 번호: {}", trimmedNode);
        }
    }

    private void setPasswordsSafely(RedisSentinelConfiguration config) {
        // Redis 패스워드
        String redisPassword = System.getenv("REDIS_PASSWORD");
        if (redisPassword != null && !redisPassword.trim().isEmpty()) {
            config.setPassword(redisPassword);
            log.info("🔐 Redis 패스워드 설정 완료");
        }

        // Sentinel 패스워드
        String sentinelPassword = System.getenv("REDIS_SENTINEL_PASSWORD");
        if (sentinelPassword != null && !sentinelPassword.trim().isEmpty()) {
            config.setSentinelPassword(sentinelPassword);
            log.info("🔐 Sentinel 패스워드 설정 완료");
        }
    }

    /**
     * 시간 계산 헬퍼 메서드들
     */

    private Duration getCacheTtl() {
        if ("dev".equals(activeProfile)) {
            return Duration.ofHours(1);
        }
        return Duration.ofHours(24);
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
