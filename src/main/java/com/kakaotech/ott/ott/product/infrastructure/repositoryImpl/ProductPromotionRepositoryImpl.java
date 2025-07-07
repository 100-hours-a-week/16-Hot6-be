package com.kakaotech.ott.ott.product.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionJpaRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductPromotionRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantJpaRepository;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductPromotionEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductPromotionRepositoryImpl implements ProductPromotionRepository {

    private final ProductPromotionJpaRepository productPromotionJpaRepository;
    private final ProductVariantJpaRepository productVariantJpaRepository;

    @Override
    @Transactional
    public ProductPromotion save(ProductPromotion promotion) {
        // 품목 존재 여부 확인
        ProductVariantEntity variantEntity = productVariantJpaRepository.findById(promotion.getVariantId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        ProductPromotionEntity entity = ProductPromotionEntity.from(promotion, variantEntity);
        ProductPromotionEntity savedEntity = productPromotionJpaRepository.save(entity);

        return savedEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPromotion findById(Long promotionId) {
        return productPromotionJpaRepository.findById(promotionId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND))    // TODO: ErrorCode 재설정
                .toDomain();
    }

    @Override
    @Transactional
    public ProductPromotion update(ProductPromotion promotion) {
        ProductPromotionEntity entity = productPromotionJpaRepository.findById(promotion.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        // 특가 정보 업데이트
        entity.setStatus(promotion.getStatus());
        entity.setType(promotion.getType());
        entity.setName(promotion.getName());
        entity.setOriginalPrice(promotion.getOriginalPrice());
        entity.setDiscountPrice(promotion.getDiscountPrice());
        entity.setRate(promotion.getRate());
        entity.setTotalQuantity(promotion.getTotalQuantity());
        entity.setReservedQuantity(promotion.getReservedQuantity());
        entity.setSoldQuantity(promotion.getSoldQuantity());
        entity.setStartAt(promotion.getStartAt());
        entity.setEndAt(promotion.getEndAt());
        entity.setMaxPerCustomer(promotion.getMaxPerCustomer());

        ProductPromotionEntity savedEntity = productPromotionJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    @Transactional
    public void delete(Long promotionId) {
        ProductPromotionEntity entity = productPromotionJpaRepository.findById(promotionId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        productPromotionJpaRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPromotion> findByVariantId(Long variantId) {
        return productPromotionJpaRepository.findByVariantEntityId(variantId)
                .stream()
                .map(ProductPromotionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPromotion findByVariantIdAndStatus(Long variantId, PromotionStatus status) {
        return productPromotionJpaRepository.findByVariantIdAndStatus(variantId, status)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPromotion> findByType(PromotionType type) {
        return productPromotionJpaRepository.findByType(type.name())
                .stream()
                .map(ProductPromotionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPromotion> findActivePromotions(LocalDateTime now) {
        return productPromotionJpaRepository.findActivePromotions(now)
                .stream()
                .map(ProductPromotionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductPromotion> findCurrentPromotion(Long variantId, LocalDateTime now) {
        return productPromotionJpaRepository.findCurrentPromotion(variantId, now)
                .map(ProductPromotionEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPromotion> findExpiredPromotions(LocalDateTime now) {
        return productPromotionJpaRepository.findExpiredPromotions(now)
                .stream()
                .map(ProductPromotionEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long promotionId, PromotionStatus status) {
        ProductPromotionEntity entity = productPromotionJpaRepository.findById(promotionId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROMOTION_NOT_FOUND));

        productPromotionJpaRepository.updateStatus(promotionId, status.name());
    }

    @Override
    @Transactional
    public void expirePromotions(List<Long> promotionIds) {
        if (promotionIds != null && !promotionIds.isEmpty()) {
            productPromotionJpaRepository.expirePromotions(promotionIds);
        }
    }

    @Override
    public List<ProductPromotion> findProductsToAutoEnded(LocalDateTime now) {

        return productPromotionJpaRepository.findProductsToAutoEnded(now)
                .stream()
                .map(ProductPromotionEntity::toDomain)
                .toList();
    }
}