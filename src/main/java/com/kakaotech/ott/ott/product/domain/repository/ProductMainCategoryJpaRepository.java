package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.infrastructure.entity.ProductMainCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductMainCategoryJpaRepository extends JpaRepository<ProductMainCategoryEntity, Long> {

    Optional<ProductMainCategoryEntity> findByName(String mainCategoryName);
}
