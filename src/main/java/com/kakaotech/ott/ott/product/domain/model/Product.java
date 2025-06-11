package com.kakaotech.ott.ott.product.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Getter
@Builder
public class Product {
    private Long id;
    private ProductType type;
    private ProductStatus status;
    private String name;
    private String description;
    private Map<String, Object> specification;  // JSON 형태로 저장될 사양 정보

    @Builder.Default
    private int salesCount = 0;

    @Builder.Default
    private int scrapCount = 0;

    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>();

    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 상품 생성 팩토리 메서드
    public static Product createProduct(
            ProductType type,
            String name,
            String description,
            Map<String, Object> specification) {

        // 비즈니스 검증
//        validateProductName(name);
//        validateDescription(description);

        return Product.builder()
                .type(type)
                .status(ProductStatus.ACTIVE)
                .name(name)
                .description(description)
                .specification(specification)
                .salesCount(0)
                .scrapCount(0)
                .build();
    }

    // 상품명 검증
    private static void validateProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("상품명은 50자를 초과할 수 없습니다.");
        }
    }

    // 상품 설명 검증
    private static void validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("상품 설명은 필수입니다.");
        }
        if (description.length() > 1000) {
            throw new IllegalArgumentException("상품 설명은 1000자를 초과할 수 없습니다.");
        }
    }

    // 상품 정보 수정
    public void updateProductInfo(String name, String description, Map<String, Object> specification) {
        validateProductName(name);
        validateDescription(description);

        this.name = name;
        this.description = description;
        this.specification = specification;
    }

    // 상품 상태 변경
    public void updateStatus(ProductStatus status) {
        this.status = status;
    }

    // 판매수 증가
    public void increaseSalesCount() {
        this.salesCount++;
    }

    // 스크랩 수 증가
    public void increaseScrapCount() {
        this.scrapCount++;
    }

    // 스크랩 수 감소
    public void decreaseScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }

    // 품목 추가
    public void addVariant(ProductVariant variant) {
        this.variants.add(variant);
    }

    // 이미지 추가
    public void addImage(ProductImage image) {
        this.images.add(image);
    }

    // 모든 이미지 제거
    public void clearImages() {
        this.images.clear();
    }
}
