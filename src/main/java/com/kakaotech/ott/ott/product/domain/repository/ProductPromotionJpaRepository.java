package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.PromotionStatus;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductPromotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductPromotionJpaRepository extends JpaRepository<ProductPromotionEntity, Long> {

    // 품목별 특가 조회
    List<ProductPromotionEntity> findByVariantEntityId(Long variantId);

    // 품목별 + 상태별 특가 조회
    @Query("SELECT p FROM ProductPromotionEntity p WHERE p.variantEntity.id = :variantId AND p.status = :status")
    Optional<ProductPromotionEntity> findByVariantIdAndStatus(@Param("variantId") Long variantId, @Param("status") PromotionStatus status);

    // 특가 타입별 조회
    @Query("SELECT p FROM ProductPromotionEntity p WHERE p.type = :type")
    List<ProductPromotionEntity> findByType(@Param("type") String type);

    // 현재 활성 특가 조회
    @Query("SELECT p FROM ProductPromotionEntity p WHERE p.status = 'ACTIVE' AND p.startAt <= :now AND p.endAt > :now")
    List<ProductPromotionEntity> findActivePromotions(@Param("now") LocalDateTime now);

    // 현재 진행 중인 품목 특가 조회
    @Query("SELECT p FROM ProductPromotionEntity p WHERE p.variantEntity = :variantId AND p.status = 'ACTIVE' AND p.startAt <= :now AND p.endAt > :now")
    Optional<ProductPromotionEntity> findCurrentPromotion(@Param("variantId") Long variantId, @Param("now") LocalDateTime now);

    // 만료된 특가 조회
    @Query("SELECT p FROM ProductPromotionEntity p WHERE p.status = 'ACTIVE' AND p.endAt <= :now")
    List<ProductPromotionEntity> findExpiredPromotions(@Param("now") LocalDateTime now);

    // 상태 업데이트
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductPromotionEntity p SET p.status = :status WHERE p.id = :id")
    void updateStatus(@Param("id") Long promotionId, @Param("status") String status);

    // 여러 특가 만료 처리
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductPromotionEntity p SET p.status = 'ENDED' WHERE p.id IN :promotionIds")
    void expirePromotions(@Param("promotionIds") List<Long> promotionIds);

    @Query("SELECT p FROM ProductPromotionEntity p " +
            "WHERE p.endAt <= :now " +
            "AND p.status <> 'ENDED'")
    List<ProductPromotionEntity> findProductsToAutoEnded(@Param("now") LocalDateTime now);
}