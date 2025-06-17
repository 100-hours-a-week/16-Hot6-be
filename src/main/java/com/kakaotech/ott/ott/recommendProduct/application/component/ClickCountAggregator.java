package com.kakaotech.ott.ott.recommendProduct.application.component;

import com.kakaotech.ott.ott.recommendProduct.domain.repository.DeskProductRepository;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class ClickCountAggregator {
    // postId -> 누적 조회수 델타
    private final ConcurrentMap<Long, AtomicLong> cache = new ConcurrentHashMap<>( );

    private final DeskProductRepository deskProductRepository;

    public ClickCountAggregator(DeskProductRepository deskProductRepository) {
        this.deskProductRepository = deskProductRepository;
    }

    public void increment(Long deskProductId) {
        System.out.println("[increment] 호출됨 - productId = " + deskProductId);

        cache.computeIfAbsent(deskProductId, id -> new AtomicLong()).incrementAndGet();
    }

    /**
     * 1분마다 실행:
     * 1) 키별로 모아둔 카운터를 snapshot으로 추출
     * 2) 원본 AtomicLong 은 0으로 리셋
     * 3) snapshot을 한 건씩 DB에 반영(update click_count = click_count + delta)
     */
    @Scheduled(fixedDelay = 60_000)
    @SchedulerLock(name = "flush-click-counts", lockAtMostFor = "PT59S")
    @Transactional
    public void flush() {
        if (cache.isEmpty()) {
            return;
        }

        // 1) snapshot & reset
        Map<Long, Long> toUpdate = new HashMap<>();
        cache.forEach((deskProductId, counter) -> {
            long delta = counter.getAndSet(0);
            if (delta > 0) {
                toUpdate.put(deskProductId, delta);
            } else {
                cache.remove(deskProductId, counter);  // 조회가 한 번도 없는 postId는 제거
            }
        });

        // 2) DB에 배치 반영
        toUpdate.forEach((deskProductId, delta) -> {
            deskProductRepository.incrementClickCount(deskProductId, delta);
        });
    }
}
