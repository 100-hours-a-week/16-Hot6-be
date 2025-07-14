package com.kakaotech.ott.ott.global.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String keyPrefix();

    String key();

    // 락 유효시간
    long leaseTime() default 5;

    // 락 타임아웃 단위
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    // 락 획득 대기 최대시간
    long waitTime() default 3;
}
