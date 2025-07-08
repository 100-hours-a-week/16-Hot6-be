package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.ProductMainCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductMainCategoryJpaRepository extends JpaRepository<ProductMainCategoryEntity, Long> {

    Optional<ProductMainCategoryEntity> findByName(String mainCategoryName);

    List<ProductMainCategoryEntity> findByNameIn(@Param("names") List<String> names);
}
