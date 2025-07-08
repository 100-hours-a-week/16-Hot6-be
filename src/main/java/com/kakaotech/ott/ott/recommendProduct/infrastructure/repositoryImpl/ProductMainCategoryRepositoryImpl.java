package com.kakaotech.ott.ott.recommendProduct.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.recommendProduct.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.ProductMainCategoryJpaRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.ProductMainCategoryRepository;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.ProductMainCategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductMainCategoryRepositoryImpl implements ProductMainCategoryRepository {

    private final ProductMainCategoryJpaRepository productMainCategoryJpaRepository;


    @Override
    public ProductMainCategory save(ProductMainCategory productMainCategory) {

        return productMainCategoryJpaRepository.findByName(productMainCategory.getName())
                .orElseGet(() -> productMainCategoryJpaRepository.save(ProductMainCategoryEntity.from(productMainCategory)))
                .toDomain();
    }

    @Override
    public Optional<ProductMainCategoryEntity> findByName(String mainCategoryName) {
        return productMainCategoryJpaRepository.findByName(mainCategoryName);
    }

    @Override
    public List<ProductMainCategory> findByNameIn(List<String> names) {
        return productMainCategoryJpaRepository.findByNameIn(names)
                .stream()
                .map(ProductMainCategoryEntity::toDomain)
                .toList();
    }
}
