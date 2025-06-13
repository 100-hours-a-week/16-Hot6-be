package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.VariantStatus;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductVariantJpaRepository extends JpaRepository<ProductVariantEntity, Long> {

    // 상품별 품목 조회
    List<ProductVariantEntity> findByProductEntityId(Long productId);

    // 상품별 + 상태별 품목 조회
    @Query("SELECT v FROM ProductVariantEntity v WHERE v.productEntity.id = :productId AND v.status = :status")
    List<ProductVariantEntity> findByProductIdAndStatus(@Param("productId") Long productId, @Param("status") String status);

    // 상품 + 품목명 중복 체크
    boolean existsByProductEntityIdAndName(Long productId, String name);

    // 품목 존재, 활성 상태 확인
    @Query("""
        SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END 
        FROM ProductVariantEntity v 
        WHERE v.id = :variantId AND v.status = 'ACTIVE'
                """)
    boolean existsByIdAndActiveStatus(@Param("variantId") Long variantId);

    // 품목이 재고 관리 가능한 상태인지 확인 (ACTIVE 또는 OUT_OF_STOCK)
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM ProductVariantEntity v WHERE v.id = :variantId AND v.status IN ('ACTIVE', 'OUT_OF_STOCK')")
    boolean existsByIdAndManageableStatus(@Param("variantId") Long variantId);

    // 판매 가능한 품목 조회 (재고 있음 + ACTIVE 상태)
    @Query("SELECT v FROM ProductVariantEntity v WHERE v.productEntity.id = :productId AND v.status = 'ACTIVE' AND v.availableQuantity > 0")
    List<ProductVariantEntity> findAvailableVariants(@Param("productId") Long productId);

    // 품목의 상태 정보 조회 (검증 및 로깅용)
    @Query("SELECT v.status FROM ProductVariantEntity v WHERE v.id = :variantId")
    Optional<VariantStatus> findStatusById(@Param("variantId") Long variantId);

    // 재고 수량 업데이트
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductVariantEntity v SET v.availableQuantity = :quantity WHERE v.id = :id")
    void updateAvailableQuantity(@Param("id") Long variantId, @Param("quantity") int quantity);

    /**
     * 활성 상태의 품목만 재고 예약 가능
     * 조건: 1) 활성 상태, 2) 충분한 재고
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ProductVariantEntity v 
        SET v.availableQuantity = v.availableQuantity - :quantity,
            v.reservedQuantity = v.reservedQuantity + :quantity
        WHERE v.id = :variantId 
        AND v.status = 'ACTIVE'
        AND v.availableQuantity >= :quantity
        """)
    int reserveStockForActiveVariant(@Param("variantId") Long variantId, @Param("quantity") int quantity);

    /**
     * 관리 가능한 상태의 품목에 대해 예약 해제
     * 조건: 1) ACTIVE 또는 OUT_OF_STOCK 상태, 2) 충분한 예약량
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ProductVariantEntity v 
        SET v.availableQuantity = v.availableQuantity + :quantity,
            v.reservedQuantity = v.reservedQuantity - :quantity
        WHERE v.id = :variantId 
        AND v.reservedQuantity >= :quantity
        """)
    int releaseReservedStockForManageableVariant(@Param("variantId") Long variantId, @Param("quantity") int quantity);

    //품목 상태 업데이트
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductVariantEntity v SET v.status = :status WHERE v.id = :variantId")
    void updateStatus(@Param("variantId") Long variantId, @Param("status") String status);
}