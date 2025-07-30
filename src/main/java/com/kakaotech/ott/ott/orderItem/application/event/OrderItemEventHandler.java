package com.kakaotech.ott.ott.orderItem.application.event;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemEventHandler {

    private final OrderItemRepository orderItemRepository;

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
}
