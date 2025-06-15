package com.kakaotech.ott.ott.product.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.model.VariantStatus;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantJpaRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import com.kakaotech.ott.ott.product.domain.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductVariantRepositoryImpl implements ProductVariantRepository {

    private final ProductVariantJpaRepository productVariantJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    @Override
    @Transactional
    public ProductVariant save(ProductVariant variant) {
        // 상품 존재 여부 확인
        ProductEntity productEntity = productJpaRepository.findById(variant.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        // 상품 + 품목명 중복 체크 (신규 생성인 경우)
        if (variant.getId() == null && existsByProductEntityIdAndName(variant.getProductId(), variant.getName())) {
            throw new CustomException(ErrorCode.DUPLICATE_PRODUCT_NAME);
        }

        ProductVariantEntity entity = ProductVariantEntity.from(variant, productEntity);
        ProductVariantEntity savedEntity = productVariantJpaRepository.save(entity);

        return savedEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariant findById(Long variantId) {
        return productVariantJpaRepository.findById(variantId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .toDomain();
    }

    @Override
    @Transactional
    public ProductVariant update(ProductVariant variant) {
        ProductVariantEntity entity = productVariantJpaRepository.findById(variant.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        // 품목 정보 업데이트
        entity.setStatus(variant.getStatus());
        entity.setName(variant.getName());
        entity.setPrice(variant.getPrice());
        entity.setAvailableQuantity(variant.getAvailableQuantity());
        entity.setReservedQuantity(variant.getReservedQuantity());
        entity.setOnPromotion(variant.isOnPromotion());

        ProductVariantEntity savedEntity = productVariantJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    @Transactional
    public void delete(Long variantId) {
        ProductVariantEntity entity = productVariantJpaRepository.findById(variantId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        productVariantJpaRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariant> findByProductId(Long productId) {
        return productVariantJpaRepository.findByProductEntityId(productId)
                .stream()
                .map(ProductVariantEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariant> findByProductIdAndStatus(Long productId, VariantStatus status) {
        return productVariantJpaRepository.findByProductIdAndStatus(productId, status.name())
                .stream()
                .map(ProductVariantEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByProductEntityIdAndName(Long productId, String name) {
        return productVariantJpaRepository.existsByProductEntityIdAndName(productId, name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariant> findAvailableVariants(Long productId) {
        return productVariantJpaRepository.findAvailableVariants(productId)
                .stream()
                .map(ProductVariantEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateQuantity(Long variantId, int quantity) {
        ProductVariantEntity entity = productVariantJpaRepository.findById(variantId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        // 수량 검증
        if (quantity < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }

        productVariantJpaRepository.updateQuantity(variantId, quantity);
    }

    @Override
    @Transactional
    public void reserveQuantity(Long variantId, int quantity) {
        ProductVariantEntity entity = productVariantJpaRepository.findById(variantId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        // 예약 가능 수량 체크
        if (entity.getReservedQuantity() < quantity) {
            throw new IllegalArgumentException("예약 가능한 수량을 초과했습니다.");
        }

        productVariantJpaRepository.reserveQuantity(variantId, quantity);
    }

    @Override
    @Transactional
    public void releaseReservedQuantity(Long variantId, int quantity) {
        ProductVariantEntity entity = productVariantJpaRepository.findById(variantId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        // 예약 해제 수량 검증
        if (entity.getReservedQuantity() < quantity) {
            throw new IllegalArgumentException("예약 해제할 수량이 예약 수량보다 큽니다.");
        }

        productVariantJpaRepository.releaseReservedQuantity(variantId, quantity);
    }
}