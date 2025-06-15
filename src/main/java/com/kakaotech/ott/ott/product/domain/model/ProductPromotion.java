package com.kakaotech.ott.ott.product.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProductPromotion {

    private Long id;
    private Long variantId;
    private PromotionStatus status;
    private PromotionType type;
    private String name;
    private int originalPrice;
    private int discountPrice;
    private BigDecimal rate;
    private int promotionQuantity;

    @Builder.Default
    private int soldQuantity = 0;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int maxPerCustomer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 특가 생성 팩토리 메서드
    public static ProductPromotion createPromotion(
            Long variantId,
            PromotionType type,
            String name,
            int originalPrice,
            int discountPrice,
            int promotionQuantity,
            LocalDateTime startAt,
            LocalDateTime endAt,
            int maxPerCustomer) {

        // 비즈니스 검증
//        validatePromotionName(name);
        BigDecimal rate = validatePrices(originalPrice, discountPrice);
//        validateRate(rate);
//        validateQuantity(allocatedQuantity);
//        validateDates(startAt, endAt);
//        validateMaxPerCustomer(maxPerCustomer);

        return ProductPromotion.builder()
                .variantId(variantId)
                .status(PromotionStatus.ACTIVE)
                .type(type)
                .name(name)
                .originalPrice(originalPrice)
                .discountPrice(discountPrice)
                .rate(rate)
                .promotionQuantity(promotionQuantity)
                .soldQuantity(0)
                .startAt(startAt)
                .endAt(endAt)
                .maxPerCustomer(maxPerCustomer)
                .build();
    }

    // 특가명 검증
    private static void validatePromotionName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("특가명은 필수입니다.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("특가명은 50자를 초과할 수 없습니다.");
        }
    }

    // 가격 검증
    private static BigDecimal validatePrices(int originalPrice, int discountPrice) {
        if (originalPrice < 0) {
            throw new IllegalArgumentException("정가는 0 이상이어야 합니다.");
        }
        if (discountPrice < 0) {
            throw new IllegalArgumentException("할인가는 0 이상이어야 합니다.");
        }
        if (discountPrice > originalPrice) {
            throw new IllegalArgumentException("할인가는 정가보다 작아야 합니다.");
        }
        BigDecimal origBd = BigDecimal.valueOf(originalPrice);
        BigDecimal discBd = BigDecimal.valueOf(discountPrice);
        BigDecimal fraction = BigDecimal.ONE
                .subtract(discBd.divide(origBd, 4, RoundingMode.HALF_UP));
        // 정수 퍼센트 계산: *100 후 소수점 이하 반올림
        BigDecimal percentBd = fraction.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP);
        return percentBd;
    }

    // 할인율 검증
    private static void validateRate(BigDecimal rate) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("할인율은 0~100 사이여야 합니다.");
        }
    }

    // 수량 검증
    private static void validateQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }
    }

    // 날짜 검증
    private static void validateDates(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null) {
            throw new IllegalArgumentException("시작일과 종료일은 필수입니다.");
        }
        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("종료일은 시작일보다 늦어야 합니다.");
        }
    }

    // 최대 구매 수량 검증
    private static void validateMaxPerCustomer(int maxPerCustomer) {
        if (maxPerCustomer < 1) {
            throw new IllegalArgumentException("1인당 최대 구매 수량은 1 이상이어야 합니다.");
        }
    }

    // 특가 상태 변경
    public void updateStatus(PromotionStatus status) {
        this.status = status;
    }

    // 판매 수량 증가
    public void increaseSoldQuantity(int quantity) {
        if (this.soldQuantity + quantity > this.promotionQuantity) {
            throw new IllegalArgumentException("특가 할당 수량을 초과할 수 없습니다.");
        }
        this.soldQuantity += quantity;

        if (this.soldQuantity == this.promotionQuantity) {
            this.status = PromotionStatus.SOLD_OUT;
        }
    }

    // 판매 수량 감소
    public void decreaseSoldQuantity(int quantity) {
        if (quantity > this.soldQuantity) {
            throw new IllegalArgumentException("판매된 수량을 초과할 수 없습니다.");
        }

        if (this.soldQuantity == this.promotionQuantity) {
            this.status = PromotionStatus.ACTIVE;
        }

        this.soldQuantity -= quantity;
    }


    // 특가 활성 여부 확인
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return this.status == PromotionStatus.ACTIVE
                && now.isAfter(startAt)
                && now.isBefore(endAt)
                && this.soldQuantity < this.promotionQuantity;
    }
}