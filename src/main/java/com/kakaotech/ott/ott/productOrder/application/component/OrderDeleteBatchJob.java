package com.kakaotech.ott.ott.productOrder.application.component;

import com.kakaotech.ott.ott.batch.application.component.BatchExecutor;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
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
public class OrderDeleteBatchJob {

    private final ProductOrderRepository productOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductPromotionRepository productPromotionRepository;
    private final ProductVariantRepository productVariantRepository;
    private final BatchExecutor batchExecutor;

    @Scheduled(cron = "0 */1 * * * ?")
    @SchedulerLock(name = "product-order-delete", lockAtMostFor = "PT2M")
    @Transactional
    public void deleteOrders() {
        batchExecutor.execute("product-order-delete", this::processDelete);
    }

    private void processDelete() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        List<ProductOrder> ordersToDelete = productOrderRepository.findOrdersToAutoDelete(threshold);
        for (ProductOrder order : ordersToDelete) {
            order.fail();
            productOrderRepository.deleteProductOrder(order);

            List<OrderItem> orderItem = orderItemRepository.findByProductOrderId(order.getId());

            for(OrderItem item : orderItem) {
                cancelReservedStock(item);
                item.fail();
                orderItemRepository.deleteOrderItem(item);
            }
        }
    }

    private void cancelReservedStock(OrderItem item) {
        if (item.getPromotionId() != null) {
            // OrderItem에 promotion_id가 있는 경우 (주문 당시 특가상품)
            ProductPromotion promotion = productPromotionRepository.findById(item.getPromotionId());
            if (promotion != null && promotion.isActive()) {
                // 특가가 현재 활성화 상태이면 예약 재고를 취소
                promotion.cancelPromotionReservation(item.getQuantity());
                productPromotionRepository.update(promotion);
            }
            // 특가가 활성화 상태가 아니면 (ENDED, CANCELED) 아무것도 하지 않음
        } else {
            // OrderItem에 promotion_id가 없는 경우 (주문 당시 일반 상품)
            ProductVariant variant = productVariantRepository.findById(item.getVariantsId());
            if (variant != null) {
                variant.cancelReservation(item.getQuantity());
                productVariantRepository.update(variant);
            }
        }
    }

}
