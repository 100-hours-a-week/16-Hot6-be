package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostWeightUpdater {
    private final PostRepository postRepository;

    @Scheduled(fixedDelay = 86_400_000) // 24시간마다 실행
    @SchedulerLock(name = "update-post-weights", lockAtMostFor = "PT23H")
    @Transactional
    public void updateWeights() {
        // ✅ Native Query를 통해 Batch Update
        postRepository.batchUpdateWeights();
    }
}
