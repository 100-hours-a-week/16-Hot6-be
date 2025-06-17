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
    @Query("SELECT v FROM ProductVariantEntity v WHERE v.productEntity.id = :productId AND v.status = 'ACTIVE' AND (v.totalQuantity - v.reservedQuantity - v.soldQuantity) > 0")
    List<ProductVariantEntity> findAvailableVariants(@Param("productId") Long productId);

    // 품목의 상태 정보 조회 (검증 및 로깅용)
    @Query("SELECT v.status FROM ProductVariantEntity v WHERE v.id = :variantId")
    Optional<VariantStatus> findStatusById(@Param("variantId") Long variantId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductVariantEntity v SET v.totalQuantity = :quantity WHERE v.id = :id")
    void updateTotalQuantity(@Param("id") Long variantId, @Param("quantity") int quantity);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductVariantEntity v SET v.reservedQuantity = :quantity WHERE v.id = :id")
    void updateReservedQuantity(@Param("id") Long variantId, @Param("quantity") int quantity);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductVariantEntity v SET v.soldQuantity = :quantity WHERE v.id = :id")
    void updateSoldQuantity(@Param("id") Long variantId, @Param("quantity") int quantity);

    /**
     * 재고 예약 (주문 생성 시)
     * - 조건: ACTIVE 상태, 충분한 재고
     * - 동작: reservedQuantity 증가, 재고 부족시 상태 변경
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ProductVariantEntity v
        SET v.reservedQuantity = v.reservedQuantity + :quantity,
        v.status = CASE
            WHEN (v.totalQuantity - v.reservedQuantity - :quantity - v.soldQuantity) = 0 THEN 'OUT_OF_STOCK'
            ELSE v.status
        END
        WHERE v.id = :variantId
        AND v.status = 'ACTIVE'
        AND (v.totalQuantity - v.reservedQuantity - v.soldQuantity) >= :quantity
        """)
    int reserveStock(@Param("variantId") Long variantId, @Param("quantity") int quantity);

    /**
     * 예약 취소 (주문 취소 시)
     * - 조건: 충분한 예약량
     * - 동작: reservedQuantity 감소, 재고 복구시 상태 변경
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ProductVariantEntity v
        SET v.reservedQuantity = v.reservedQuantity - :quantity,
        v.status = CASE
            WHEN v.status = 'OUT_OF_STOCK' AND (v.totalQuantity - v.reservedQuantity + :quantity - v.soldQuantity) > 0 THEN 'ACTIVE'
            ELSE v.status
        END
        WHERE v.id = :variantId
        AND v.reservedQuantity >= :quantity
        """)
    int cancelReservation(@Param("variantId") Long variantId, @Param("quantity") int quantity);

    /**
     * 판매 확정 (결제 완료 시)
     * - 조건: 충분한 예약량
     * - 동작: reservedQuantity 감소, soldQuantity 증가
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ProductVariantEntity v
        SET v.reservedQuantity = v.reservedQuantity - :quantity,
        v.soldQuantity = v.soldQuantity + :quantity
        WHERE v.id = :variantId
        AND v.reservedQuantity >= :quantity
        """)
    int confirmSale(@Param("variantId") Long variantId, @Param("quantity") int quantity);

    /**
     * 판매 취소 (환불 시 - 배송 전)
     * - 조건: 충분한 판매량
     * - 동작: soldQuantity 감소, 재고 복구시 상태 변경
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ProductVariantEntity v
        SET v.soldQuantity = v.soldQuantity - :quantity,
        v.status = CASE
            WHEN v.status = 'OUT_OF_STOCK' AND (v.totalQuantity - v.reservedQuantity - v.soldQuantity + :quantity) > 0 THEN 'ACTIVE'
            ELSE v.status
        END
        WHERE v.id = :variantId
        AND v.soldQuantity >= :quantity
        """)
    int cancelSale(@Param("variantId") Long variantId, @Param("quantity") int quantity);

    /**
     * 재고 추가 (입고 시)
     * - 동작: totalQuantity 증가, 재고 생성시 상태 변경
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ProductVariantEntity v
        SET v.totalQuantity = v.totalQuantity + :quantity,
        v.status = CASE
            WHEN v.status = 'OUT_OF_STOCK' AND (v.totalQuantity + :quantity - v.reservedQuantity - v.soldQuantity) > 0 THEN 'ACTIVE'
            ELSE v.status
        END
        WHERE v.id = :variantId
        """)
    int addStock(@Param("variantId") Long variantId, @Param("quantity") int quantity);

    /**
     * 재고 차감 (손실, 파손 등)
     * - 동작: totalQuantity 감소, 재고 부족시 상태 변경
     */
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE ProductVariantEntity v
        SET v.totalQuantity = v.totalQuantity - :quantity,
        v.status = CASE
            WHEN (v.totalQuantity - :quantity - v.reservedQuantity - v.soldQuantity) = 0 THEN 'OUT_OF_STOCK'
            ELSE v.status
        END
        WHERE v.id = :variantId
        AND v.totalQuantity >= :quantity
        AND (v.totalQuantity - :quantity) >= (v.reservedQuantity + v.soldQuantity)
        """)
    int reduceStock(@Param("variantId") Long variantId, @Param("quantity") int quantity);

    //품목 상태 업데이트
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductVariantEntity v SET v.status = :status WHERE v.id = :variantId")
    void updateStatus(@Param("variantId") Long variantId, @Param("status") String status);
}