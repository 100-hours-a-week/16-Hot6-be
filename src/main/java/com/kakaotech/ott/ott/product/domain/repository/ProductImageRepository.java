package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.ProductImage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProductImageRepository {

    // 기본 CRUD
    ProductImage save(ProductImage image);

    ProductImage findById(Long imageId);

    void delete(Long imageId);

    // 품목별 이미지 조회
    List<ProductImage> findByVariantId(Long variantId);

    List<ProductImage> findByVariantIdOrderBySequence(Long variantId);

    // 비즈니스 메서드들
    void deleteByVariantId(Long variantId);
//
    int countByVariantId(Long variantId);

    ProductImage findMainImage(Long productId);

    // 시퀀스 관리
    void updateSequence(Long imageId, int sequence);

    void reorderSequences(Long variantId, List<Long> imageIds);

    Map<Long, ProductImage> findByVariantIdIn(List<Long> variantsIds);

}