package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.ProductSubCategory;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductMainCategoryEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductSubCategoryEntity;

import java.util.Optional;

public interface ProductSubCategoryRepository {

    ProductSubCategoryEntity save(ProductSubCategory productSubCategory, ProductMainCategoryEntity productMainCategoryEntity);

    Optional<ProductSubCategoryEntity> findByName(String subCategoryName);

    ProductSubCategory findById(Long subCategoryId);
}
