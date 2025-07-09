package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.recommendProduct.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.ProductMainCategoryEntity;

import java.util.List;
import java.util.Optional;

public interface ProductMainCategoryRepository {

    ProductMainCategory save(ProductMainCategory productMainCategory);

    Optional<ProductMainCategoryEntity> findByName(String mainCategoryName);

    List<ProductMainCategory> findByNameIn(List<String> names);
}
