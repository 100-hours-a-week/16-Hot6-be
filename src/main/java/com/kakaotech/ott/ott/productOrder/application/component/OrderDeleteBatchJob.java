package com.kakaotech.ott.ott.productOrder.application.component;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderDeleteBatchJob {

    private final ProductOrderRepository productOrderRepository;
    private final OrderItemRepository orderItemRepository;

    @Scheduled(cron = "0 */1 * * * ?")
    @Transactional
    public void deleteOrders() {

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<ProductOrder> ordersToDelete = productOrderRepository.findOrdersToAutoDelete(threshold);

        for (ProductOrder order : ordersToDelete) {
            order.fail();
            productOrderRepository.deleteProductOrder(order);

            List<OrderItem> orderItem = orderItemRepository.findByProductOrderId(order.getId());

            for(OrderItem item : orderItem) {
                item.fail();
                orderItemRepository.deleteOrderItem(item);
            }
        }
    }
}
