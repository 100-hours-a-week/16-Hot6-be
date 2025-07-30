package com.kakaotech.ott.ott.productOrder.application.event;

import com.kakaotech.ott.ott.batch.domain.model.BatchJobLog;
import com.kakaotech.ott.ott.batch.domain.repository.BatchJobLogRepository;
import com.kakaotech.ott.ott.orderItem.application.event.OrderItemCompletedEvent;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductOrderEventHandler {

    private final ProductOrderRepository productOrderRepository;
    private final BatchJobLogRepository batchJobLogRepository;

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

    @Recover
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recover(Exception e, OrderItemCompletedEvent event) {
        log.error("❌ [ProductOrderEventHandler] 3회 재시도 실패 - orderId={}, 이유={}", event.getProductOrderId(), e.getMessage(), e);

        BatchJobLog batchJobLog = BatchJobLog.createBatchJobLog("ProductOrderEventHandler - " + event.getProductOrderId(), LocalDateTime.now());
        batchJobLog.markFailed(e.getMessage());

        batchJobLogRepository.save(batchJobLog);
    }

}
