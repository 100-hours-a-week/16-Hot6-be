package com.kakaotech.ott.ott.productOrder.application.component;

import com.kakaotech.ott.ott.batch.application.component.BatchExecutor;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderConfirmBatchJob {
    private final ProductOrderRepository productOrderRepository;
    private final BatchExecutor batchExecutor;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 새벽 3시
    @SchedulerLock(name = "product-order-confirm", lockAtMostFor = "PT2M")
    @Transactional
    public void confirmOrders() {
        batchExecutor.execute("product-order-confirm", this::processConfirm);

    }

    private void processConfirm() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(3);
        List<ProductOrder> ordersToConfirm = productOrderRepository.findOrdersToAutoConfirm(threshold);
        for (ProductOrder order : ordersToConfirm) {
            order.confirm();
            productOrderRepository.confirmProductOrder(order);
        }
    }
}
