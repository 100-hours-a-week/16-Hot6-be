package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.Product;
import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductImageEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

    // 상품명 중복 체크
    boolean existsByName(String name);

    // 상품 타입별 조회
    @Query("SELECT s FROM ProductEntity s WHERE s.type = :type ORDER BY s.createdAt DESC")
    Slice<ProductEntity> findByTypeOrderByCreatedAtDesc(@Param("type") String type, Pageable pageable);

    // 상품 상태별 조회
    @Query("SELECT s FROM ProductEntity s WHERE s.status = :status ORDER BY s.createdAt DESC")
    Slice<ProductEntity> findByStatusOrderByCreatedAtDesc(@Param("status") String status, Pageable pageable);

    // 판매순 Top N 조회
    @Query("SELECT s FROM ProductEntity s ORDER BY s.salesCount DESC")
    List<ProductEntity> findByOrderBySalesCountDesc(Pageable pageable);

    // 스크랩순 Top N 조회
    @Query("SELECT s FROM ProductEntity s ORDER BY s.scrapCount DESC")
    List<ProductEntity> findByOrderByScrapCountDesc(Pageable pageable);


    // 전체 일반 상품 목록 조회(특가 제외, 최신순 정렬)
    @Query("""
    SELECT DISTINCT p FROM ProductEntity p 
    LEFT JOIN FETCH p.variants v 
    WHERE p.status = 'ACTIVE' 
    AND v.status = 'ACTIVE' 
    AND v.isOnPromotion = false 
    AND (:lastProductId IS NULL OR p.id < :lastProductId) 
    ORDER BY p.createdAt DESC
    """)
    List<ProductEntity> findAllProductsByCursor(
            @Param("lastProductId") Long lastProductId,
            Pageable pageable);

    // Variant ID 리스트로 첫 번째 이미지들만 조회
    @Query("""
    SELECT img FROM ProductImageEntity img 
    WHERE img.variantEntity.id IN :variantIds 
    AND img.sequence = 1 
    ORDER BY img.variantEntity.id
    """)
    List<ProductImageEntity> findFirstImagesByVariantIds(@Param("variantIds") List<Long> variantIds);

    // 상품 타입별 목록 조회(특가 제외, 최신순 정렬)
    @Query("""
        SELECT DISTINCT p FROM ProductEntity p 
        LEFT JOIN FETCH p.variants v 
        WHERE p.status = 'ACTIVE' 
        AND p.type = :productType 
        AND v.status = 'ACTIVE' 
        AND v.isOnPromotion = false 
        AND (:lastProductId IS NULL OR p.id < :lastProductId) 
        ORDER BY p.createdAt DESC
        """)
    List<ProductEntity> findProductsByTypeByCursor(
            @Param("productType") ProductType productType,
            @Param("lastProductId") Long lastProductId,
            Pageable pageable);

    // 특가 상품 목록 조회(할인 종료 시간 기준 오름차순 정렬; 마감 임박 순)
    @Query("""
    SELECT DISTINCT p FROM ProductEntity p 
    LEFT JOIN FETCH p.variants v 
    WHERE p.status = 'ACTIVE' 
    AND v.status = 'ACTIVE' 
    AND v.isOnPromotion = true 
    AND EXISTS (
        SELECT 1 FROM ProductPromotionEntity pr 
        WHERE pr.variantEntity = v 
        AND pr.status = 'ACTIVE' 
        AND pr.type = :promotionType 
        AND pr.endAt > CURRENT_TIMESTAMP
    )
    AND (:lastProductId IS NULL OR p.id > :lastProductId) 
    ORDER BY (
        SELECT MIN(pr2.endAt) 
        FROM ProductPromotionEntity pr2 
        WHERE pr2.variantEntity = v 
        AND pr2.status = 'ACTIVE' 
        AND pr2.type = :promotionType 
        AND pr2.endAt > CURRENT_TIMESTAMP
    ) ASC, p.id ASC
    """)
    List<ProductEntity> findPromotionProductsByCursor(
            @Param("promotionType") PromotionType promotionType,
            @Param("lastProductId") Long lastProductId,
            Pageable pageable);

    // 판매수 증가/감소
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductEntity s SET s.salesCount = GREATEST(0, s.salesCount + :delta) WHERE s.id = :id")
    void incrementSalesCount(@Param("id") Long productId, @Param("delta") Long delta);

    // 스크랩수 증가/감소
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
    UPDATE ProductEntity p 
    SET p.scrapCount = GREATEST(0, p.scrapCount + :delta) 
    WHERE p.id = :id
    """)
    void incrementScrapCount(@Param("id") Long productId, @Param("delta") Long delta);
}