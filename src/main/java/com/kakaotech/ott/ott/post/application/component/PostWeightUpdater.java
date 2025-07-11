package com.kakaotech.ott.ott.post.application.component;

import com.kakaotech.ott.ott.batch.application.component.BatchExecutor;
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
    private final BatchExecutor batchExecutor;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "update-post-weights", lockAtMostFor = "PT23H")
    @Transactional
    public void updateWeights() {
        batchExecutor.execute("update-post-weights", this::processUpdate);
    }

    private void processUpdate() {
        postRepository.batchUpdateWeights();
    }
}
