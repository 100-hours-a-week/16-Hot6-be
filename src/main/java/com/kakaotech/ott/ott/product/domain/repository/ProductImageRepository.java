package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository {

    // 기본 CRUD
    ProductImage save(ProductImage image);

    Optional<ProductImage> findById(Long imageId);

    void delete(Long imageId);

    // 상품별 이미지 조회
    List<ProductImage> findByProductId(Long productId);

    List<ProductImage> findByProductIdOrderBySequence(Long productId);

    // 비즈니스 메서드들
    void deleteByProductId(Long productId);
//
    int countByProductId(Long productId);

    Optional<ProductImage> findMainImage(Long productId);

    // 시퀀스 관리
    void updateSequence(Long imageId, int sequence);

    void reorderSequences(Long productId, List<Long> imageIds);
}