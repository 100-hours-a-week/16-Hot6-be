package com.kakaotech.ott.ott.util.scheduler;

import com.kakaotech.ott.ott.global.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheEvictionScheduler {
    private final RedisTemplate<String, String> redisTemplate;

    // 매일 00:01 및 13:01 실행
    @Scheduled(cron = "0 1 0,13 * * *", zone = "Asia/Seoul")
    public void evictMainPageCaches() {
        deleteKeysByPattern(RedisConfig.MAIN_CACHE + "*");
        log.info("Today promotion products, Popular setups and Recommend items caches have been evicted.");
    }

    private void deleteKeysByPattern(String pattern) {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/delete-keys-by-pattern.lua")));
        script.setResultType(String.class);

        String cursor = "0";
        int scanCount = 100; // 한 번에 스캔하고 삭제할 키의 개수

        do {
            cursor = redisTemplate.execute(
                    script,
                    Collections.singletonList(cursor), // KEYS[1]
                    pattern,                           // ARGV[1]
                    String.valueOf(scanCount)          // ARGV[2]
            );
        } while (!cursor.equals("0"));
        log.info("Successfully deleted keys with pattern: {}", pattern);
    }
}