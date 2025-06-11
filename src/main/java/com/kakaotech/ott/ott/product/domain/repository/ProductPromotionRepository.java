package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductPromotionRepository {

    // 기본 CRUD
    ProductPromotion save(ProductPromotion promotion);

    Optional<ProductPromotion> findById(Long promotionId);

    ProductPromotion update(ProductPromotion promotion);

    void delete(Long promotionId);

    // 품목별 특가 조회
    List<ProductPromotion> findByVariantId(Long variantId);

    List<ProductPromotion> findByVariantIdAndStatus(Long variantId, PromotionStatus status);

    // 특가 타입별 조회
    List<ProductPromotion> findByType(PromotionType type);

    List<ProductPromotion> findActivePromotions(LocalDateTime now);

    // 비즈니스 메서드들
    Optional<ProductPromotion> findCurrentPromotion(Long variantId, LocalDateTime now);

    List<ProductPromotion> findExpiredPromotions(LocalDateTime now);

    // 판매 수량 증가
    void increaseSoldQuantity(Long promotionId, int quantity);

    // 상태 업데이트
    void updateStatus(Long promotionId, PromotionStatus status);

    void expirePromotions(List<Long> promotionIds);
}