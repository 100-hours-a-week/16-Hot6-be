package com.kakaotech.ott.ott.product.application.component;

import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
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

    @Scheduled(cron = "0 0 */1 * * ?") //
    @Transactional
    public void confirmOrders() {
        LocalDateTime now = LocalDateTime.now();
        List<ProductPromotion> promotionToEnded = productPromotionRepository.findProductsToAutoEnded(now);
        for (ProductPromotion promotion : promotionToEnded) {
            promotion.updateStatus(PromotionStatus.ENDED);
            productPromotionRepository.update(promotion);

            ProductVariant productVariant = productVariantRepository.findById(promotion.getVariantId());
            productVariant.setPromotionStatus(false);
            productVariantRepository.update(productVariant);
            System.out.println("배치 완료");
        }


    }
}
