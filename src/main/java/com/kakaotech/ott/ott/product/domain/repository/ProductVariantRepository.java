package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.model.VariantStatus;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository {

    // 기본 CRUD
    ProductVariant save(ProductVariant variant);

    ProductVariant findById(Long variantId);

    ProductVariant update(ProductVariant variant);

    void delete(Long variantId);

    // 상품별 품목 조회
    List<ProductVariant> findByProductId(Long productId);

    List<ProductVariant> findByProductIdAndStatus(Long productId, VariantStatus status);

    // 비즈니스 메서드들
    boolean existsByProductEntityIdAndName(Long productId, String name);

    List<ProductVariant> findAvailableVariants(Long productId);

    // 재고 관리
    void updateQuantity(Long variantId, int quantity);

    void reserveQuantity(Long variantId, int quantity);

    void releaseReservedQuantity(Long variantId, int quantity);
}