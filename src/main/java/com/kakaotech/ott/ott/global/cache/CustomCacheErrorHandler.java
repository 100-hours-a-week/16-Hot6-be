package com.kakaotech.ott.ott.global.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.NonNull;

@Slf4j
public class CustomCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key) {
        log.error(
                "[Cache] Unable to get from cache: " + cache.getName() + ", key: " + key,
                exception
        );
    }

    @Override
    public void handleCachePutError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key, Object value) {
        log.error(
                "[Cache] Unable to put into cache: " + cache.getName() + ", key: " + key,
                exception
        );
    }

    @Override
    public void handleCacheEvictError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key) {
        log.error(
                "[Cache] Unable to evict from cache: " + cache.getName() + ", key: " + key,
                exception
        );
    }

    @Override
    public void handleCacheClearError(@NonNull RuntimeException exception, @NonNull Cache cache) {
        log.error(
                "[Cache] Unable to clear cache: " + cache.getName(),
                exception
        );
    }
}
