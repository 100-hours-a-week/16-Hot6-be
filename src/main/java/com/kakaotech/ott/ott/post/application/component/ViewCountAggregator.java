package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.batch.application.component.BatchExecutor;
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
    private final BatchExecutor batchExecutor;

    private static final String POST_VIEW_COUNT_CACHE = "postViewCount";

    public void increment(Long postId) {
        redisTemplate.opsForHash().increment(POST_VIEW_COUNT_CACHE, String.valueOf(postId), 1);
    }

    @Scheduled(fixedDelay = 60_000)
    @SchedulerLock(name = "flush-posts-view-counts", lockAtMostFor = "PT59S")
    @Transactional
    public void flush() {
        batchExecutor.execute("flush-posts-view-counts", this::processFlush);
    }

    private void processFlush() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(POST_VIEW_COUNT_CACHE);

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            Long postId = Long.parseLong(entry.getKey().toString());
            Long delta = Long.parseLong(entry.getValue().toString());

            if (delta > 0) {
                postRepository.incrementViewCount(postId, delta);
            }
        }

        redisTemplate.delete(POST_VIEW_COUNT_CACHE);
    }
}