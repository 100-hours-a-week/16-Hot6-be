package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ProductMainCategoryEntity;
import com.kakaotech.ott.ott.user.domain.model.User;

import java.util.Optional;

public interface ProductMainCategoryRepository {

    ProductMainCategoryEntity save(ProductMainCategory productMainCategory);

    Optional<ProductMainCategoryEntity> findByName(String mainCategoryName);
}
