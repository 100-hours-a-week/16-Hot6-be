package com.kakaotech.ott.ott.product.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ProductVariant {

    private Long id;
    private Long productId;
    private VariantStatus status;
    private String name;
    private int price;
    private int availableQuantity;

    @Builder.Default
    private int reservedQuantity = 0;

    @Builder.Default
    private boolean isOnPromotion = false;

    @Builder.Default
    private List<ProductPromotion> promotions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 품목 생성 팩토리 메서드
    public static ProductVariant createVariant(
            Long productId,
            String name,
            Integer price,
            Integer availableQuantity) {

        // 비즈니스 검증
//        validateVariantName(name);
//        validatePrice(price);
//        validateQuantity(quantityAvailable);

        return ProductVariant.builder()
                .productId(productId)
                .status(VariantStatus.ACTIVE)
                .name(name)
                .price(price)
                .availableQuantity(availableQuantity)
                .reservedQuantity(0)
                .isOnPromotion(false)
                .build();
    }

    // 품목명 검증
    private static void validateVariantName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("품목명은 필수입니다.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("품목명은 50자를 초과할 수 없습니다.");
        }
    }

    // 가격 검증
    private static void validatePrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다.");
        }
    }

    // 수량 검증
    private static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }
    }

    // 품목 정보 수정
    public void updateVariantInfo(String name, int price, int quantityAvailable) {
        validateVariantName(name);
        validatePrice(price);
        validateQuantity(quantityAvailable);

        this.name = name;
        this.price = price;
        this.availableQuantity = quantityAvailable;
    }

    // 품목 상태 변경
    public void updateStatus(VariantStatus status) {
        this.status = status;
    }

    // 재고 수량 차감
    public void decreaseQuantity(int quantity) {
        if (this.availableQuantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.availableQuantity -= quantity;

        if (this.availableQuantity == 0) {
            this.status = VariantStatus.OUT_OF_STOCK;
        }
    }

    // 재고 수량 증가
    public void increaseQuantity(int quantity) {
        validateQuantity(quantity);
        this.availableQuantity += quantity;

        if (this.status == VariantStatus.OUT_OF_STOCK && this.availableQuantity > 0) {
            this.status = VariantStatus.ACTIVE;
        }
    }

    // 특가 상태 설정
    public void setPromotionStatus(boolean isOnPromotion) {
        this.isOnPromotion = isOnPromotion;
    }

    // 특가 추가
    public void addPromotion(ProductPromotion promotion) {
        this.promotions.add(promotion);
        this.isOnPromotion = true;
    }
}