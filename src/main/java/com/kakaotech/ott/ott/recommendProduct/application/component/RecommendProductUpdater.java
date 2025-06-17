package com.kakaotech.ott.ott.recommendProduct.application.component;

import com.kakaotech.ott.ott.recommendProduct.domain.repository.DeskProductRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RecommendProductUpdater {

    private final DeskProductRepository deskProductRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "update-desk-products-weights", lockAtMostFor = "PT23H")
    @Transactional
    public void updateWeights() {
        // ✅ Native Query를 통해 Batch Update
        deskProductRepository.batchUpdateWeights();
    }
}
