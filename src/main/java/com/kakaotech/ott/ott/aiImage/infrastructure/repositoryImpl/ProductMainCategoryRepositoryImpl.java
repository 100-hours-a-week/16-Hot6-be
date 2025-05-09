package com.kakaotech.ott.ott.aiImage.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.aiImage.domain.repository.ProductMainCategoryJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.ProductMainCategoryRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ProductMainCategoryEntity;
import com.kakaotech.ott.ott.user.domain.model.User;
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
