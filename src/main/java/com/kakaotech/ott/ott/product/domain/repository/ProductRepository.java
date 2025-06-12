package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.domain.model.ProductType;
import com.kakaotech.ott.ott.product.domain.model.Product;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    // 기본 CRUD
    Product save(Product product);

    Optional<Product> findById(Long productId);

    Product update(Product product);

    void delete(Long productId);

    // 조회 메서드들
    List<Product> findAll();

    Slice<Product> findAllByType(ProductType type, Long cursorId, int size);

    Slice<Product> findAllByStatus(String status, Long cursorId, int size);

    // 비즈니스 메서드들
    boolean existsByName(String name);

    List<Product> findTop10BySalesCount();

    List<Product> findTop10ByScrapCount();

    // 카운트 증감 메서드들
    void incrementSalesCount(Long productId);

    void decrementSalesCount(Long productId);

    void incrementScrapCount(Long productId, Long delta);
}