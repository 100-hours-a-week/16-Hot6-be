package com.kakaotech.ott.ott.recommendProduct.application.component;

import com.kakaotech.ott.ott.recommendProduct.domain.repository.DeskProductRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ClickCountAggregator {

    private final RedisTemplate<String, Object> redisTemplate;
    private final DeskProductRepository deskProductRepository;

    private static final String PRODUCT_CLICK_COUNT_CACHE = "productClickCount";

    public void increment(Long deskProductId) {
        redisTemplate.opsForHash().increment(PRODUCT_CLICK_COUNT_CACHE, String.valueOf(deskProductId), 1);
    }

    /**
     * 1분마다 실행:
     * 1) Redis에서 Snapshot 가져옴
     * 2) Redis 값 제거
     * 3) DB 반영
     */
    @Scheduled(fixedDelay = 60_000)
    @SchedulerLock(name = "flush-click-counts", lockAtMostFor = "PT59S")
    @Transactional
    public void flush() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(PRODUCT_CLICK_COUNT_CACHE);

        if (entries.isEmpty()) return;

        entries.forEach((deskIdObj, countObj) -> {
            Long deskId = Long.parseLong(deskIdObj.toString());
            Long delta = Long.parseLong(countObj.toString());

            if (delta > 0) {
                deskProductRepository.incrementClickCount(deskId, delta);
            }
        });

        // flush 후 초기화
        redisTemplate.delete(PRODUCT_CLICK_COUNT_CACHE);
    }
}
