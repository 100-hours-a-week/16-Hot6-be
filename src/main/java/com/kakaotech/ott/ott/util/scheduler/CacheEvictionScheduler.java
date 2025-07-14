package com.kakaotech.ott.ott.util.scheduler;

import com.kakaotech.ott.ott.global.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheEvictionScheduler {
    private final CacheManager cacheManager;

    // 매일 자정에 실행되어 캐시 삭제(인기, 추천, 홈 데이터)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void evictExpiredCaches() {
        evictCache(RedisConfig.POPULAR_SETUPS_CACHE);
        evictCache(RedisConfig.RECOMMEND_ITEMS_CACHE);
        evictCache(RedisConfig.HOME_DATA_CACHE);
    }

    // 매일 오후 1시에 실행
    @Scheduled(cron = "0 0 13 * * *", zone = "Asia/Seoul")
    public void evictCachesAt13PM() {
        evictCache(RedisConfig.TODAY_PROMOTION_CACHE);
        evictCache(RedisConfig.HOME_DATA_CACHE);
    }

    private void evictCache(String cacheName) {
        try {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
        } catch (NullPointerException e) {
            log.warn("Cache '{}' not found or is null. Skipping eviction.", cacheName);
        } catch (Exception e) {
            log.error("Error while evicting cache '{}'", cacheName, e);
        }
    }

}
