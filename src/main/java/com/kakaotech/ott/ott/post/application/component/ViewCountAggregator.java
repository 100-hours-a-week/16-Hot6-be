package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ViewCountAggregator {

    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;

    private static final String POST_VIEW_COUNT_CACHE = "postViewCount";

    public void increment(Long postId) {
        redisTemplate.opsForHash().increment(POST_VIEW_COUNT_CACHE, String.valueOf(postId), 1);
    }

    /**
     * 1분마다 실행:
     * 1) Redis에서 Snapshot 가져옴
     * 2) Redis 값 제거
     * 3) DB 반영
     */
    @Scheduled(fixedDelay = 60_000)
    @SchedulerLock(name = "flush-view-counts", lockAtMostFor = "PT59S")
    @Transactional
    public void flush() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(POST_VIEW_COUNT_CACHE);

        if (entries.isEmpty()) return;

        entries.forEach((postIdObj, countObj) -> {
            Long postId = Long.parseLong(postIdObj.toString());
            Long delta = Long.parseLong(countObj.toString());

            if (delta > 0) {
                postRepository.incrementViewCount(postId, delta);
            }
        });

        // flush 후 초기화
        redisTemplate.delete(POST_VIEW_COUNT_CACHE);
    }
}