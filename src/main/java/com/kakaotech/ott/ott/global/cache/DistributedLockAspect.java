package com.kakaotech.ott.ott.global.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAspect {
    private final RedissonClient redissonClient;
    private final CacheManager cacheManager;
    private final SpelKeyGenerator spelKeyGenerator;

    @Around("@annotation(com.kakaotech.ott.ott.global.cache.DistributedLock) && @annotation(org.springframework.cache.annotation.Cacheable)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        Cacheable cacheable = method.getAnnotation(Cacheable.class);

        // 1. 캐시와 락 키 생성
        String cacheName = cacheable.cacheNames()[0];
        Object dynamicKey = spelKeyGenerator.generateKey(distributedLock.key(), signature, joinPoint.getArgs());
        String lockKey = "lock:" + distributedLock.keyPrefix() + "::" + dynamicKey;

        // 2. Redisson RLock 객체 가져오기
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 3. 락 획득 시도 (waitTime, leaseTime 적용)
            boolean isLockAcquired = lock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

            if (!isLockAcquired) {
                // 4. 락 획득 최종 실패
                log.error("Failed to acquire lock: {}", lockKey);
                throw new RuntimeException("Failed to acquire lock for cache regeneration");
            }

            // 5. 락 획득 성공 후, 캐시 다시 확인 (Double-checked locking)
            // 락을 기다리는 동안 다른 스레드/인스턴스가 캐시를 생성했을 수 있음
            Cache cache = cacheManager.getCache(cacheName);
            Object cachedValue = (cache != null) ? cache.get(dynamicKey, Object.class) : null;
            if (cachedValue != null) {
                log.info("Found cache after acquiring lock: {}", lockKey);
                return cachedValue;
            }

            // 6. 캐시가 여전히 없으면, 대상 메소드 실행 (DB 조회)
            log.info("Lock acquired. Proceeding to target method for cache regeneration: {}", lockKey);
            return joinPoint.proceed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lock acquisition interrupted", e);
        } finally {
            // 7. 락 해제 (락을 획득한 스레드만 해제 시도)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
