package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductVariantJpaRepository extends JpaRepository<ProductVariantEntity, Long> {

    // 상품별 품목 조회
    List<ProductVariantEntity> findByProductEntityId(Long productId);

    // 상품별 + 상태별 품목 조회
    @Query("SELECT v FROM ProductVariantEntity v WHERE v.productEntity.id = :productId AND v.status = :status")
    List<ProductVariantEntity> findByProductIdAndStatus(@Param("productId") Long productId, @Param("status") String status);

    // 상품 + 품목명 중복 체크
    boolean existsByProductEntityIdAndName(Long productId, String name);

    // 판매 가능한 품목 조회 (재고 있음 + ACTIVE 상태)
    @Query("SELECT v FROM ProductVariantEntity v WHERE v.productEntity.id = :productId AND v.status = 'ACTIVE' AND v.availableQuantity > 0")
    List<ProductVariantEntity> findAvailableVariants(@Param("productId") Long productId);

    // 재고 수량 업데이트
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductVariantEntity v SET v.availableQuantity = :quantity WHERE v.id = :id")
    void updateQuantity(@Param("id") Long variantId, @Param("quantity") int quantity);

    // 예약 수량 증가
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductVariantEntity v SET v.reservedQuantity = v.reservedQuantity + :quantity WHERE v.id = :id")
    void reserveQuantity(@Param("id") Long variantId, @Param("quantity") int quantity);

    // 예약 수량 해제
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductVariantEntity v SET v.reservedQuantity = GREATEST(0, v.reservedQuantity - :quantity) WHERE v.id = :id")
    void releaseReservedQuantity(@Param("id") Long variantId, @Param("quantity") int quantity);
}