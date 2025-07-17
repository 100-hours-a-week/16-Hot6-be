package com.kakaotech.ott.ott.recommendProduct.application.component;

import com.kakaotech.ott.ott.batch.application.component.BatchExecutor;
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
    private final BatchExecutor batchExecutor;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @SchedulerLock(name = "update-desk-products-weights", lockAtMostFor = "PT5M")
    @Transactional
    public void updateWeights() {
        batchExecutor.execute("update-desk-products-weights", this::processUpdateWeight);

    }

    private void processUpdateWeight() {
        deskProductRepository.batchUpdateWeights();
    }

}
