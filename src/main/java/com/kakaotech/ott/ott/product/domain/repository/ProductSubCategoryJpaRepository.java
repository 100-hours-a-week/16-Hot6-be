package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.infrastructure.entity.ProductMainCategoryEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductSubCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductSubCategoryJpaRepository extends JpaRepository<ProductSubCategoryEntity, Long> {

    Optional<ProductSubCategoryEntity> findByName(String subCategoryName);

    Optional<ProductSubCategoryEntity> findByMainCategoryAndName(ProductMainCategoryEntity mainCategory, String name);
}
