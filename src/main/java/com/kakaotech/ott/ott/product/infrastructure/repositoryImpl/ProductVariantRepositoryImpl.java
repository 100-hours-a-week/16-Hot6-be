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
                .map(ProductVariantEntity::toDomain)
                .orElseThrow(() -> new CustomException(ErrorCode.VARIANT_NOT_FOUND));
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
    public void updateAvailableQuantity(Long variantId, int availableQuantity) {
        // 1. 입력 검증
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }

        // 2. 품목 존재 여부 확인
        validateVariantExists(variantId);
        productVariantJpaRepository.updateAvailableQuantity(variantId, availableQuantity);
    }

    @Override
    @Transactional
    public void reserveStock(Long variantId, int quantity) {
        // 1. 입력 검증
        validateQuantity(quantity);

        // 2. 품목 존재 여부 및 활성 상태 확인
        validateVariantExistsAndActive(variantId);

        try {
            // 3. 재고 예약 실행
            int updatedRows = productVariantJpaRepository.reserveStockForActiveVariant(variantId, quantity);

            // 4. 업데이트 실패 시 상세 원인 확인 후 예외 발생
            if (updatedRows == 0) {
                throw new IllegalArgumentException("재고 예약 실패");
            }

        } catch (CustomException | IllegalArgumentException e) {
            throw e; // 이미 처리된 예외는 그대로 재발생
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void releaseReservedStock(Long variantId, int quantity) {
        // 1. 입력 검증
        validateQuantity(quantity);

        // 2. 품목 존재 여부 확인
        validateVariantExists(variantId);

        try {
            // 3. 예약 해제 실행
            int updatedRows = productVariantJpaRepository.releaseReservedStockForManageableVariant(variantId, quantity);

            // 4. 업데이트 실패 시 상세 원인 확인 후 예외 발생
            if (updatedRows == 0) {
                throw new IllegalArgumentException("재고 예약 실패");
            }

        } catch (CustomException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    // == private Method ==
    // 수량 검증
    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("예약 수량은 0보다 커야 합니다.");
        }
    }

    // 품목 존재 확인
    private void validateVariantExists(Long variantId) {
        if (!productVariantJpaRepository.existsById(variantId)) {
            throw new CustomException(ErrorCode.VARIANT_NOT_FOUND);
        }
    }

    // 품목 존재 여부 + 활성 상태 확인 (재고 예약용)
    private void validateVariantExistsAndActive(Long variantId) {
        if (!productVariantJpaRepository.existsByIdAndActiveStatus(variantId)) {
            Optional<VariantStatus> status = productVariantJpaRepository.findStatusById(variantId);
            if (status.isEmpty()) {
                throw new CustomException(ErrorCode.VARIANT_NOT_FOUND);
            } else {
                throw new CustomException(ErrorCode.INVALID_VARIANT_STATUS);
            }
        }
    }

    // 품목 존재 여부 + 관리 가능 상태 확인 (예약 해제/확정용)
    private void validateVariantExistsAndManageable(Long variantId) {
        if (!productVariantJpaRepository.existsByIdAndManageableStatus(variantId)) {
            Optional<VariantStatus> status = productVariantJpaRepository.findStatusById(variantId);
            if (status.isEmpty()) {
                throw new CustomException(ErrorCode.VARIANT_NOT_FOUND);
            } else {
                throw new CustomException(ErrorCode.INVALID_VARIANT_STATUS);
            }
        }
    }

}