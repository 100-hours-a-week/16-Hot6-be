package com.kakaotech.ott.ott.aiImage.domain.repository;

import com.kakaotech.ott.ott.aiImage.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.aiImage.domain.model.ProductSubCategory;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ProductMainCategoryEntity;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ProductSubCategoryEntity;

import java.util.Optional;

public interface ProductSubCategoryRepository {

    ProductSubCategoryEntity save(ProductSubCategory productSubCategory, ProductMainCategoryEntity productMainCategoryEntity);

    Optional<ProductSubCategoryEntity> findByName(String subCategoryName);
}
