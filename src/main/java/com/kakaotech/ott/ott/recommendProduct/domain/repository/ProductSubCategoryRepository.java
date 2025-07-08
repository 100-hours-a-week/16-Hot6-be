package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.recommendProduct.domain.model.ProductSubCategory;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.ProductSubCategoryEntity;

import java.util.List;
import java.util.Optional;

public interface ProductSubCategoryRepository {

    ProductSubCategory save(ProductSubCategory productSubCategory);

    Optional<ProductSubCategoryEntity> findByName(String subCategoryName);

    ProductSubCategory findById(Long subCategoryId);

    List<ProductSubCategory> findByNameIn(List<String> names);
}
