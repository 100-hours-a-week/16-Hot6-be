package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
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
public class ViewCountAggregator {

    // postId -> 누적 조회수 델타
    private final ConcurrentMap<Long, AtomicLong> cache = new ConcurrentHashMap<>( );

    private final PostRepository postRepository;

    public ViewCountAggregator(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void increment(Long postId) {
        cache.computeIfAbsent(postId, id -> new AtomicLong()).incrementAndGet();
    }

    /**
     * 1분마다 실행:
     * 1) 키별로 모아둔 카운터를 snapshot으로 추출
     * 2) 원본 AtomicLong 은 0으로 리셋
     * 3) snapshot을 한 건씩 DB에 반영(update view_count = view_count + delta)
     */
    @Scheduled(fixedDelay = 60_000)
    @SchedulerLock(name = "flush-view-counts", lockAtMostFor = "PT59S")
    @Transactional
    public void flush() {
        if (cache.isEmpty()) {
            return;
        }

        // 1) snapshot & reset
        Map<Long, Long> toUpdate = new HashMap<>();
        cache.forEach((postId, counter) -> {
            long delta = counter.getAndSet(0);
            if (delta > 0) {
                toUpdate.put(postId, delta);
            } else {
                cache.remove(postId, counter);  // 조회가 한 번도 없는 postId는 제거
            }
        });

        // 2) DB에 배치 반영
        toUpdate.forEach((postId, delta) -> {
            postRepository.incrementViewCount(postId, delta);
        });
    }
}