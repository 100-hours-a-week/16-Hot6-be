package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductMainCategoryEntity;

import java.util.Optional;

public interface ProductMainCategoryRepository {

    ProductMainCategoryEntity save(ProductMainCategory productMainCategory);

    Optional<ProductMainCategoryEntity> findByName(String mainCategoryName);
}
