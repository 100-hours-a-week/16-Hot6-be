package com.kakaotech.ott.ott.productOrder.application.event;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductOrderEventHandler {

    private final ProductOrderRepository productOrderRepository;

    @Async
    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductOrderCompleted(ProductOrderCompletedEvent event) {

        ProductOrder order = productOrderRepository.findById(event.getProductOrderId());
        order.pay();
        productOrderRepository.paymentOrder(order);
    }

}
