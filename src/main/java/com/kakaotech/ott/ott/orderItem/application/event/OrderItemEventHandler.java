package com.kakaotech.ott.ott.orderItem.application.event;

import com.kakaotech.ott.ott.batch.domain.model.BatchJobLog;
import com.kakaotech.ott.ott.batch.domain.repository.BatchJobLogRepository;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
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
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderItemEventHandler {

    private final OrderItemRepository orderItemRepository;
    private final BatchJobLogRepository batchJobLogRepository;

    @Async
    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderItemCompleted(OrderItemCompletedEvent event) {

        List<OrderItem> items = orderItemRepository.findByProductOrderId(event.getProductOrderId());
        for (OrderItem item : items) {
            if (item.getStatus().equals(OrderItemStatus.PENDING)) {
                item.pay();
            }
        }

        orderItemRepository.payOrderItem(items);
    }

    @Recover
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recover(Exception e, OrderItemCompletedEvent event) {
        log.error("❌ [OrderItemEventHandler] 3회 재시도 실패 - orderId={}, 이유={}", event.getProductOrderId(), e.getMessage(), e);

        BatchJobLog batchJobLog = BatchJobLog.createBatchJobLog("OrderItemCompletedEvent - " + event.getProductOrderId(), LocalDateTime.now());
        batchJobLog.markFailed(e.getMessage());

        batchJobLogRepository.save(batchJobLog);
    }
}
