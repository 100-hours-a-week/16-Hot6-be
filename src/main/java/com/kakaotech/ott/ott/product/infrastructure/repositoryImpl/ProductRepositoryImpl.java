package com.kakaotech.ott.ott.product.infrastructure.repositoryImpl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.product.domain.model.Product;
import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import com.kakaotech.ott.ott.product.domain.model.VariantStatus;
import com.kakaotech.ott.ott.product.domain.repository.ProductJpaRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductRepository;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductImageEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public Product save(Product product) {
        ProductEntity productEntity = ProductEntity.from(product);
        ProductEntity savedEntity = productJpaRepository.save(productEntity);
        return savedEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long productId) {
        return productJpaRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND))
                .toDomain();
    }

    @Override
    @Transactional
    public Product update(Product product) {
        ProductEntity entity = productJpaRepository.findById(product.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        // 상품 정보 업데이트
        entity.setType(product.getType());
        entity.setStatus(product.getStatus());
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setSpecification(product.getSpecification());

        ProductEntity savedEntity = productJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        ProductEntity entity = productJpaRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        productJpaRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productJpaRepository.findAll()
                .stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<Product> findAllByType(ProductType type, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        Slice<ProductEntity> entitySlice = productJpaRepository
                .findByTypeOrderByCreatedAtDesc(type.name(), pageable);

        return entitySlice.map(ProductEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Slice<Product> findAllByStatus(String status, Long cursorId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        Slice<ProductEntity> entitySlice = productJpaRepository
                .findByStatusOrderByCreatedAtDesc(status, pageable);

        return entitySlice.map(ProductEntity::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return productJpaRepository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findTop10BySalesCount() {
        Pageable pageable = PageRequest.of(0, 10);

        return productJpaRepository.findByOrderBySalesCountDesc(pageable)
                .stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findTop10ByScrapCount() {
        Pageable pageable = PageRequest.of(0, 10);

        return productJpaRepository.findByOrderByScrapCountDesc(pageable)
                .stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findProductsByCursor(ProductType productType, Long lastProductId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<ProductEntity> productEntities;

        if (productType == null) {
            // 전체 상품 조회
            productEntities = productJpaRepository.findAllProductsByCursor(lastProductId, pageable);
        } else {
            // 상품 타입별 조회
            productEntities = productJpaRepository.findProductsByTypeByCursor(productType, lastProductId, pageable);
        }

        if (productEntities.isEmpty()) {
            return Collections.emptyList();
        }

        // 2단계: 첫 번째 variant의 ID 수집
        List<Long> firstVariantIds = productEntities.stream()
                .flatMap(product -> product.getVariants().stream())
                .filter(variant -> variant.getStatus() == VariantStatus.ACTIVE)
                .collect(Collectors.groupingBy(
                        variant -> variant.getProductEntity().getId(),
                        Collectors.minBy(Comparator.comparing(ProductVariantEntity::getId))
                ))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(ProductVariantEntity::getId)
                .collect(Collectors.toList());

        // 3단계: 첫 번째 이미지들 조회
        List<ProductImageEntity> firstImages = productJpaRepository.findFirstImagesByVariantIds(firstVariantIds);

        // 4단계: 이미지를 variant에 매핑
        Map<Long, ProductImageEntity> imageMap = firstImages.stream()
                .collect(Collectors.toMap(
                        img -> img.getVariantEntity().getId(),
                        img -> img,
                        (existing, replacement) -> existing // 중복 시 첫 번째 유지
                ));

        // 5단계: Domain 객체 생성
        return productEntities.stream()
                .map(entity -> {
                    Product product = entity.toDomain();

                    // 첫 번째 variant에 이미지 설정
                    product.getVariants().stream()
                            .filter(ProductVariant::isActive)
                            .min(Comparator.comparing(ProductVariant::getId))
                            .ifPresent(firstVariant -> {
                                ProductImageEntity imageEntity = imageMap.get(firstVariant.getId());
                                if (imageEntity != null) {
                                    firstVariant.getImages().clear();
                                    firstVariant.addImage(imageEntity.toDomain());
                                }
                            });

                    return product;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findPromotionProductsByCursor(PromotionType promotionType, Long lastProductId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<ProductEntity> entities = productJpaRepository.findPromotionProductsByCursor(
                promotionType, lastProductId, pageable);

        return entities.stream()
                .map(ProductEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void incrementSalesCount(Long productId) {
        ProductEntity entity = productJpaRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        productJpaRepository.incrementSalesCount(productId, 1L);
    }

    @Override
    @Transactional
    public void decrementSalesCount(Long productId) {
        ProductEntity entity = productJpaRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        productJpaRepository.incrementSalesCount(productId, -1L);
    }

    @Override
    @Transactional
    public void incrementScrapCount(Long productId, Long delta) {
        ProductEntity productEntity = productJpaRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)); // 적절한 에러코드로 변경 필요

        if(productEntity.getScrapCount() + delta < 0)
            return;

        productJpaRepository.incrementScrapCount(productId, 1L);
    }

}