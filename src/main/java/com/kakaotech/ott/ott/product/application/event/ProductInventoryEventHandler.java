package com.kakaotech.ott.ott.product.application.event;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
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
public class ProductInventoryEventHandler {

    private final ProductVariantRepository productVariantRepository;
    private final ProductPromotionRepository productPromotionRepository;
    private final OrderItemRepository orderItemRepository;

    @Async
    @Retryable(
            value = Exception.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlerProductInventoryUpdateHandler(ProductInventoryUpdateEvent event) {
        List<OrderItem> items = orderItemRepository.findByProductOrderId(event.getProductOrderId());
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
        }
    }
}
