package com.kakaotech.ott.ott.recommendProduct.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.recommendProduct.domain.model.ProductSubCategory;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.ProductMainCategoryJpaRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.ProductSubCategoryJpaRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.ProductSubCategoryRepository;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.ProductMainCategoryEntity;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.ProductSubCategoryEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductSubCategoryRepositoryImpl implements ProductSubCategoryRepository {

    private final ProductSubCategoryJpaRepository productSubCategoryJpaRepository;
    private final ProductMainCategoryJpaRepository productMainCategoryJpaRepository;

    @Override
    public ProductSubCategory save(ProductSubCategory productSubCategory) {

        ProductMainCategoryEntity productMainCategoryEntity = productMainCategoryJpaRepository.findById(productSubCategory.getMainCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.MAIN_CATEGORY_NOT_FOUND));

        return productSubCategoryJpaRepository.findByMainCategoryAndName(productMainCategoryEntity, productSubCategory.getName())
                .orElseGet(() -> productSubCategoryJpaRepository.save(ProductSubCategoryEntity.from(productSubCategory, productMainCategoryEntity)))
                .toDomain();

    }

    @Override
    public Optional<ProductSubCategoryEntity> findByName(String subCategoryName) {

        return productSubCategoryJpaRepository.findByName(subCategoryName);
    }

    @Override
    public ProductSubCategory findById(Long subCategoryId) {

        return productSubCategoryJpaRepository.findById(subCategoryId)
                .orElseThrow(() -> new EntityNotFoundException("해당 카테고리가 존재하지 않습니다."))
                .toDomain();
    }

    @Override
    public List<ProductSubCategory> findByNameIn(List<String> names) {
        return productSubCategoryJpaRepository.findByNameIn(names)
                .stream()
                .map(ProductSubCategoryEntity::toDomain)
                .toList();
    }
}
