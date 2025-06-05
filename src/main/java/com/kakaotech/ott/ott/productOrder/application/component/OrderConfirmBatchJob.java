package com.kakaotech.ott.ott.productOrder.application.component;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderConfirmBatchJob {
    private final ProductOrderRepository productOrderRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 새벽 3시
    public void confirmOrders() {
        List<ProductOrder> ordersToConfirm = productOrderRepository.findOrdersToAutoConfirm(LocalDateTime.now());
        for (ProductOrder order : ordersToConfirm) {
            order.confirm();
            productOrderRepository.confirmProductOrder(order);
        }
    }
}
