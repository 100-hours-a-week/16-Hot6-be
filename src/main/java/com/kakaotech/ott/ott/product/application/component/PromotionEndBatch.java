package com.kakaotech.ott.ott.product.application.component;

import com.kakaotech.ott.ott.batch.application.component.BatchExecutor;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PromotionEndBatch {

    private final ProductVariantRepository productVariantRepository;
    private final ProductPromotionRepository productPromotionRepository;
    private final BatchExecutor batchExecutor;

    @Scheduled(cron = "0 0 */1 * * ?")
    @SchedulerLock(name = "product-promotion-end", lockAtMostFor = "PT2M")
    @Transactional
    public void endPromotion() {
        batchExecutor.execute("product-promotion-end", this::processEnd);
    }

    private void processEnd() {
        LocalDateTime now = LocalDateTime.now();
        List<ProductPromotion> promotionToEnded = productPromotionRepository.findProductsToAutoEnded(now);
        for (ProductPromotion promotion : promotionToEnded) {
            promotion.updateStatus(PromotionStatus.ENDED);
            productPromotionRepository.update(promotion);

            ProductVariant productVariant = productVariantRepository.findById(promotion.getVariantId());
            productVariant.setPromotionStatus(false);
            productVariant.confirmSale(promotion.getSoldQuantity());
            productVariant.cancelReservation(promotion.getTotalQuantity() - promotion.getSoldQuantity());
            productVariantRepository.update(productVariant);
        }
    }
}
