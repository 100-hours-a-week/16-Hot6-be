package com.kakaotech.ott.ott.product.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.product.domain.model.ProductImage;
import com.kakaotech.ott.ott.product.domain.repository.*;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductImageEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductImageRepositoryImpl implements ProductImageRepository {

    private final ProductImageJpaRepository productImageJpaRepository;
    private final ProductVariantJpaRepository productVariantJpaRepository;

    @Override
    @Transactional
    public ProductImage save(ProductImage image) {
        // 품목 존재 여부 확인
        ProductVariantEntity productVariantEntity = productVariantJpaRepository.findById(image.getVariantId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        // 최대 이미지 개수 체크 (5개 제한)
        int currentImageCount = countByVariantId(image.getVariantId());
        if (image.getId() == null && currentImageCount >= 5) {
            throw new IllegalArgumentException("상품 이미지는 최대 5개까지만 등록 가능합니다.");
        }

        ProductImageEntity entity = ProductImageEntity.from(image, productVariantEntity);
        ProductImageEntity savedEntity = productImageJpaRepository.save(entity);

        return savedEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductImage findById(Long imageId) {
        return productImageJpaRepository.findById(imageId)
                .map(ProductImageEntity::toDomain)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    @Override
    @Transactional
    public void delete(Long imageId) {
        ProductImageEntity entity = productImageJpaRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        productImageJpaRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImage> findByVariantId(Long variantId) {
        return productImageJpaRepository.findByVariantEntityIdOrderBySequence(variantId)
                .stream()
                .map(ProductImageEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImage> findByVariantIdOrderBySequence(Long variantId) {
        return productImageJpaRepository.findByVariantEntityIdOrderBySequence(variantId)
                .stream()
                .map(ProductImageEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByVariantId(Long variantId) {
        productImageJpaRepository.deleteByVariantEntityId(variantId);
    }

    @Override
    @Transactional(readOnly = true)
    public int countByVariantId(Long variantId) {
        return productImageJpaRepository.countByVariantEntityId(variantId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductImage findMainImage(Long productId) {
        return productImageJpaRepository.findMainImage(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .toDomain();
    }

    @Override
    @Transactional
    public void updateSequence(Long imageId, int sequence) {
        ProductImageEntity entity = productImageJpaRepository.findById(imageId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        // 시퀀스 검증
        if (sequence < 1) {
            throw new IllegalArgumentException("이미지 순서는 1 이상이어야 합니다.");
        }

        productImageJpaRepository.updateSequence(imageId, sequence);
    }

    @Override
    @Transactional
    public void reorderSequences(Long productId, List<Long> imageIds) {
        // 품목 존재 여부 확인
        productVariantJpaRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        // 이미지 ID들이 해당 상품에 속하는지 확인
        List<ProductImageEntity> existingImages = productImageJpaRepository.findByVariantEntityIdOrderBySequence(productId);
        List<Long> existingImageIds = existingImages.stream()
                .map(ProductImageEntity::getId)
                .collect(Collectors.toList());

        for (Long imageId : imageIds) {
            if (!existingImageIds.contains(imageId)) {
                throw new IllegalArgumentException("해당 상품에 속하지 않는 이미지 ID가 포함되어 있습니다: " + imageId);
            }
        }

        // 순서대로 시퀀스 업데이트
        for (int i = 0; i < imageIds.size(); i++) {
            productImageJpaRepository.updateSequence(imageIds.get(i), i + 1);
        }
    }
}