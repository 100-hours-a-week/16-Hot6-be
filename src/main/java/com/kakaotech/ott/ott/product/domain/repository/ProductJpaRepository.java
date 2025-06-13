package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.Product;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductEntity;
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