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
    private int totalQuantity;

    @Builder.Default
    private int reservedQuantity = 0;
    @Builder.Default
    private int soldQuantity = 0;

    @Builder.Default
    private boolean isOnPromotion = false;

    @Builder.Default
    private List<ProductPromotion> promotions = new ArrayList<>();

    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    // 실제 판매 가능 수량 계산 (총 수량 - 예약 수량 - 판매 수량)
    public int getAvailableQuantity() {
        return totalQuantity - reservedQuantity - soldQuantity;
    }

    // 재고 충분여부 확인
    public boolean hasAvailableStock(int requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }

    // 품목 생성 팩토리 메서드
    public static ProductVariant createVariant(
            Long productId,
            String name,
            Integer price,
            Integer totalQuantity) {

        // 비즈니스 검증
//        validateVariantName(name);
//        validatePrice(price);
//        validateQuantity(quantityAvailable);

        return ProductVariant.builder()
                .productId(productId)
                .status(VariantStatus.ACTIVE)
                .name(name)
                .price(price)
                .totalQuantity(totalQuantity)
                .reservedQuantity(0)
                .soldQuantity(0)
                .isOnPromotion(false)
                .build();
    }

    // 재고 예약 (주문 생성 시)
    public void reserveStock(int quantity) {
        validateQuantity(quantity);

        if (!hasAvailableStock(quantity)) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        this.reservedQuantity += quantity;

        // 재고 부족 시 상태 변경
        if (getAvailableQuantity() == 0) {
            this.status = VariantStatus.OUT_OF_STOCK;
        }
    }

    // 예약 취소 (주문 취소 시)
    public void cancelReservation(int quantity) {
        validateQuantity(quantity);

        if (quantity > this.reservedQuantity) {
            throw new IllegalArgumentException("예약된 수량을 초과할 수 없습니다.");
        }

        this.reservedQuantity -= quantity;

        // 재고가 다시 생긴 경우 상태 복구
        if (this.status == VariantStatus.OUT_OF_STOCK && getAvailableQuantity() > 0) {
            this.status = VariantStatus.ACTIVE;
        }
    }

    // 판매 확정 (결제 완료 시)
    public void confirmSale(int quantity) {
        validateQuantity(quantity);

        if (quantity > this.reservedQuantity) {
            throw new IllegalArgumentException("예약된 수량을 초과할 수 없습니다.");
        }

        this.reservedQuantity -= quantity;
        this.soldQuantity += quantity;
    }

    // 판매 취소 (환불 시)
    public void cancelSale(int quantity) {
        validateQuantity(quantity);

        if (quantity > this.soldQuantity) {
            throw new IllegalArgumentException("판매된 수량을 초과할 수 없습니다.");
        }

        this.soldQuantity -= quantity;

        // 재고가 다시 생긴 경우 상태 복구
        if (this.status == VariantStatus.OUT_OF_STOCK && getAvailableQuantity() > 0) {
            this.status = VariantStatus.ACTIVE;
        }
    }

    // 총 재고 추가 (입고 시)
    public void addStock(int quantity) {
        validateQuantity(quantity);
        this.totalQuantity += quantity;

        // 재고가 생긴 경우 상태 복구
        if (this.status == VariantStatus.OUT_OF_STOCK && getAvailableQuantity() > 0) {
            this.status = VariantStatus.ACTIVE;
        }
    }

    // 총 재고 차감 (손실, 파손 등)
    public void reduceStock(int quantity) {
        validateQuantity(quantity);

        // 최소한 예약된 수량 + 판매된 수량은 유지해야 함
        int minimumRequired = this.reservedQuantity + this.soldQuantity;
        if (this.totalQuantity - quantity < minimumRequired) {
            throw new IllegalArgumentException("총 재고는 최소 " + minimumRequired + "개 이상이어야 합니다.");
        }

        this.totalQuantity -= quantity;

        // 재고 부족 시 상태 변경
        if (getAvailableQuantity() == 0) {
            this.status = VariantStatus.OUT_OF_STOCK;
        }
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
    public void updateVariantInfo(String name, int price) {
        validateVariantName(name);
        validatePrice(price);

        this.name = name;
        this.price = price;
    }

    // 품목 상태 변경
    public void updateStatus(VariantStatus status) {
        this.status = status;
    }

    // 특가 상태 설정
    public void setPromotionStatus(boolean isOnPromotion) {
        this.isOnPromotion = isOnPromotion;
    }

    // 특가 추가
    public void addPromotion(ProductPromotion promotion) {
        this.promotions.add(promotion);
    }

    public boolean isActive() {
        return (this.status == VariantStatus.ACTIVE
                || this.status == VariantStatus.OUT_OF_STOCK) && getAvailableQuantity() > 0;
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