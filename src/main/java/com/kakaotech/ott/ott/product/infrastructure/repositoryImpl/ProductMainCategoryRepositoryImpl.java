package com.kakaotech.ott.ott.product.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.product.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.product.domain.repository.ProductMainCategoryJpaRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductMainCategoryRepository;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductMainCategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductMainCategoryRepositoryImpl implements ProductMainCategoryRepository {

    private final ProductMainCategoryJpaRepository productMainCategoryJpaRepository;


    @Override
    public ProductMainCategoryEntity save(ProductMainCategory productMainCategory) {

        return productMainCategoryJpaRepository.findByName(productMainCategory.getName())
                .orElseGet(() -> productMainCategoryJpaRepository.save(ProductMainCategoryEntity.from(productMainCategory)));
    }

    @Override
    public Optional<ProductMainCategoryEntity> findByName(String mainCategoryName) {
        return productMainCategoryJpaRepository.findByName(mainCategoryName);
    }
}
