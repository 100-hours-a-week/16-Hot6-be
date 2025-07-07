package com.kakaotech.ott.ott.product.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.domain.model.VariantStatus;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantJpaRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import com.kakaotech.ott.ott.product.domain.repository.ProductJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ProductVariantRepositoryImpl implements ProductVariantRepository {

    private final ProductVariantJpaRepository productVariantJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    @Override
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
    public ProductVariant findById(Long variantId) {
        return productVariantJpaRepository.findByIdWithProduct(variantId)
                .map(entity -> {
                    ProductVariant variant = entity.toDomain();
                    // Product 객체를 설정
                    variant.setProduct(entity.getProductEntity().toDomain());
                    return variant;
                })
                .orElseThrow(() -> new CustomException(ErrorCode.VARIANT_NOT_FOUND));
    }

    @Override
    public ProductVariant update(ProductVariant variant) {
        ProductVariantEntity entity = productVariantJpaRepository.findById(variant.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.VARIANT_NOT_FOUND));

        // 품목 정보 업데이트
        entity.setStatus(variant.getStatus());
        entity.setName(variant.getName());
        entity.setPrice(variant.getPrice());
        entity.setTotalQuantity(variant.getTotalQuantity());
        entity.setReservedQuantity(variant.getReservedQuantity());
        entity.setSoldQuantity(variant.getSoldQuantity());
        entity.setOnPromotion(variant.isOnPromotion());

        ProductVariantEntity savedEntity = productVariantJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public void delete(Long variantId) {
        ProductVariantEntity entity = productVariantJpaRepository.findById(variantId)
                .orElseThrow(() -> new CustomException(ErrorCode.VARIANT_NOT_FOUND));

        productVariantJpaRepository.delete(entity);
    }

    @Override
    public List<ProductVariant> findByProductId(Long productId) {
        return productVariantJpaRepository.findByProductEntityId(productId)
                .stream()
                .map(ProductVariantEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductVariant> findByProductIdAndStatus(Long productId, VariantStatus status) {
        return productVariantJpaRepository.findByProductIdAndStatus(productId, status.name())
                .stream()
                .map(ProductVariantEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByProductEntityIdAndName(Long productId, String name) {
        return productVariantJpaRepository.existsByProductEntityIdAndName(productId, name);
    }

    @Override
    public List<ProductVariant> findAvailableVariants(Long productId) {
        return productVariantJpaRepository.findAvailableVariants(productId)
                .stream()
                .map(ProductVariantEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void updateTotalQuantity(Long variantId, int totalQuantity) {
        // 1. 입력 검증
        if (totalQuantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다.");
        }

        // 2. 품목 존재 여부 확인
        validateVariantExists(variantId);
        productVariantJpaRepository.updateTotalQuantity(variantId, totalQuantity);
    }

    @Override
    public void reserveStock(Long variantId, int quantity) {
        // 1. 입력 검증
        validateQuantity(quantity);

        // 2. 품목 존재 여부 및 활성 상태 확인
        validateVariantExistsAndActive(variantId);

        try {
            // 3. 재고 예약 실행
            int updatedRows = productVariantJpaRepository.reserveStock(variantId, quantity);

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
    public void cancelReservation(Long variantId, int quantity) {
        // 1. 입력 검증
        validateQuantity(quantity);

        // 2. 품목 존재 여부 확인
        validateVariantExists(variantId);

        try {
            // 3. 예약 해제 실행
            int updatedRows = productVariantJpaRepository.cancelReservation(variantId, quantity);

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

    @Override
    public List<ProductVariant> findNormalVariantsByCursor(ProductType productType, Long lastVariantId, int size) {
        List<ProductVariantEntity> entities = productVariantJpaRepository.findNormalVariantsByCursor(
                productType, lastVariantId, PageRequest.of(0, size));

        return entities.stream()
                .map(entity -> {
                    ProductVariant variant = entity.toDomain();
                    variant.setProduct(entity.getProductEntity().toDomain());
                    return variant;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductVariant> findPromotionVariantsByCursor(PromotionType promotionType, Long lastVariantId, int size) {
        // 1단계: Variant와 Product만 조회
        List<ProductVariantEntity> entities = productVariantJpaRepository.findPromotionVariantsByCursor(
                promotionType, lastVariantId, PageRequest.of(0, size));

        if (entities.isEmpty()) {
            return Collections.emptyList();
        }

        // 2단계: Variant ID 수집
        List<Long> variantIds = entities.stream()
                .map(ProductVariantEntity::getId)
                .collect(Collectors.toList());

        // 3단계: 이미지와 프로모션을 별도 쿼리로 조회
        Map<Long, ProductVariantEntity> variantWithImagesMap = productVariantJpaRepository
                .findVariantsWithImages(variantIds).stream()
                .collect(Collectors.toMap(ProductVariantEntity::getId, Function.identity()));

        Map<Long, ProductVariantEntity> variantWithPromotionsMap = productVariantJpaRepository
                .findVariantsWithPromotions(variantIds).stream()
                .collect(Collectors.toMap(ProductVariantEntity::getId, Function.identity()));

        // 4단계: 결과 조합
        return entities.stream()
                .map(entity -> {
                    ProductVariant variant = entity.toDomain();
                    variant.setProduct(entity.getProductEntity().toDomain());

                    // 이미지 정보 설정
                    ProductVariantEntity entityWithImages = variantWithImagesMap.get(entity.getId());
                    if (entityWithImages != null && entityWithImages.getImages() != null) {
                        // clearImages() 후 addImage() 사용
                        variant.clearImages();
                        entityWithImages.getImages().forEach(imageEntity ->
                                variant.addImage(imageEntity.toDomain())
                        );
                    }

                    // 프로모션 정보 설정
                    ProductVariantEntity entityWithPromotions = variantWithPromotionsMap.get(entity.getId());
                    if (entityWithPromotions != null && entityWithPromotions.getPromotions() != null) {
                        // 기존 promotions 리스트를 clear하고 새로 추가
                        variant.getPromotions().clear();
                        entityWithPromotions.getPromotions().forEach(promotionEntity ->
                                variant.addPromotion(promotionEntity.toDomain())
                        );
                    }

                    return variant;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void incrementScrapCount(Long variantId, Long delta) {
        ProductVariantEntity productVariantEntity = productVariantJpaRepository.findById(variantId)
                .orElseThrow(() -> new CustomException(ErrorCode.VARIANT_NOT_FOUND));
        Long productId =productVariantEntity.getProductEntity().getId();

        ProductEntity productEntity = productJpaRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if(productEntity.getScrapCount() + delta < 0)
            return;

        productJpaRepository.incrementScrapCount(productId, delta);
    }
}