package com.kakaotech.ott.ott.aiImage.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.aiImage.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.aiImage.domain.model.ProductSubCategory;
import com.kakaotech.ott.ott.aiImage.domain.repository.ProductMainCategoryJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.ProductSubCategoryJpaRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.ProductSubCategoryRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ProductMainCategoryEntity;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.ProductSubCategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductSubCategoryRepositoryImpl implements ProductSubCategoryRepository {

    private final ProductSubCategoryJpaRepository productSubCategoryJpaRepository;

    @Override
    public ProductSubCategoryEntity save(ProductSubCategory productSubCategory, ProductMainCategoryEntity productMainCategoryEntity) {

        // 이미 서비스에서 Entity로 변환된 값을 넘기므로 그대로 사용
        return productSubCategoryJpaRepository.findByMainCategoryAndName(productMainCategoryEntity, productSubCategory.getName())
                .orElseGet(() -> productSubCategoryJpaRepository.save(ProductSubCategoryEntity.from(productSubCategory, productMainCategoryEntity)));

    }

    @Override
    public Optional<ProductSubCategoryEntity> findByName(String subCategoryName) {

        return productSubCategoryJpaRepository.findByName(subCategoryName);
    }
}
