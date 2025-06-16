package com.kakaotech.ott.ott.product.infrastructure.repositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.kakaotech.ott.ott.product.domain.model.*;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductImageEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductVariantEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.product.domain.repository.ProductJpaRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductRepository;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductEntity;

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

        List<ProductEntity> entities;

        if (productType == null) {
            // 전체 상품 조회
            entities = productJpaRepository.findAllProductsByCursor(lastProductId, pageable);
        } else {
            // 상품 타입별 조회
            entities = productJpaRepository.findProductsByTypeByCursor(productType, lastProductId, pageable);
        }

        return entities.stream()
                .map(ProductEntity::toDomain)
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