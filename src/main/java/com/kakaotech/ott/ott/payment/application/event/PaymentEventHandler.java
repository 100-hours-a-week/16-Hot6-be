package com.kakaotech.ott.ott.payment.application.event;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final ProductOrderRepository productOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductPromotionRepository productPromotionRepository;

    @Async
    @EventListener
    @Transactional
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        ProductOrder order = productOrderRepository.findById(event.getOrderId());
        order.pay();

        List<OrderItem> items = orderItemRepository.findByProductOrderId(order.getId());
        for (OrderItem item : items) {
            if (item.getPromotionId() != null) {
                ProductPromotion promo = productPromotionRepository.findById(item.getPromotionId());
                promo.confirmPromotionSale(item.getQuantity());
                productPromotionRepository.update(promo);
            } else {
                ProductVariant variant = productVariantRepository.findById(item.getVariantsId());
                variant.confirmSale(item.getQuantity());
                productVariantRepository.update(variant);
            }
            if (item.getStatus().equals(OrderItemStatus.PENDING)) {
                item.pay();
            }
        }

        productOrderRepository.paymentOrder(order);
        orderItemRepository.payOrderItem(items);
    }
}

