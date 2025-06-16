package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.infrastructure.entity.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ProductImageJpaRepository extends JpaRepository<ProductImageEntity, Long> {

    // 상품별 이미지 조회 (시퀀스 순)
    List<ProductImageEntity> findByProductEntityIdOrderBySequence(Long productId);

    // 상품별 이미지 삭제
    @Modifying
    @Transactional
    void deleteByProductEntityId(Long productId);

    // 상품별 이미지 개수 카운트
    int countByProductEntityId(Long productId);

    // 메인 이미지 조회 (sequence = 1)
    @Query("SELECT i FROM ProductImageEntity i WHERE i.productEntity.id = :productId AND i.sequence = 1")
    Optional<ProductImageEntity> findMainImage(@Param("productId") Long productId);

    // 시퀀스 업데이트
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE ProductImageEntity i SET i.sequence = :sequence WHERE i.id = :id")
    void updateSequence(@Param("id") Long imageId, @Param("sequence") int sequence);
}