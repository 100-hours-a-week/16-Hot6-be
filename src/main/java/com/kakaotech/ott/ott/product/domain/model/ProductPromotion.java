package com.kakaotech.ott.ott.product.domain.model;

import lombok.AllArgsConstructor;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
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
    private int totalQuantity;

    private int reservedQuantity;
    private int soldQuantity;

    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private int maxPerCustomer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void isAvailableForPurchase(LocalDateTime now) {
        if (now.isBefore(this.startAt))
            throw new CustomException(ErrorCode.SALE_NOT_STARTED);
    }

    // 판매 가능 수량 (총 수량 - 예약수량 - 판매수량)
    public int getAvailableQuantity() {
        return totalQuantity - reservedQuantity - soldQuantity;
    }
    // 특가 재고 충분여부 확인

    public boolean hasAvailableStock(int requestedQuantity) {
        return getAvailableQuantity() >= requestedQuantity;
    }

    // 특가 생성 팩토리 메서드
    public static ProductPromotion createPromotion(
            Long variantId,
            PromotionType type,
            String name,
            int originalPrice,
            int discountPrice,
            int totalQuantity,
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
                .totalQuantity(totalQuantity)
                .reservedQuantity(0)
                .soldQuantity(0)
                .startAt(startAt)
                .endAt(endAt)
                .maxPerCustomer(maxPerCustomer)
                .build();
    }

    // 특가 재고 예약 (주문 생성 시)
    public void reservePromotionStock(int quantity) {
        validateQuantity(quantity);

        if (!hasAvailableStock(quantity)) {
            throw new IllegalArgumentException("특가 재고가 부족합니다.");
        }

        this.reservedQuantity += quantity;

        // 특가 재고 부족 시 상태 변경
        if (getAvailableQuantity() == 0) {
            this.status = PromotionStatus.SOLD_OUT;
        }
    }

    // 특가 예약 취소 (주문 취소 시)
    public void cancelPromotionReservation(int quantity) {
        validateQuantity(quantity);

        if (quantity > this.reservedQuantity) {
            throw new IllegalArgumentException("예약된 특가 수량을 초과할 수 없습니다.");
        }

        this.reservedQuantity -= quantity;

        // 특가 재고가 다시 생긴 경우 상태 복구
        if (this.status == PromotionStatus.SOLD_OUT && getAvailableQuantity() > 0) {
            this.status = PromotionStatus.ACTIVE;
        }
    }

    // 특가 판매 확정 (결제 완료 시)
    public void confirmPromotionSale(int quantity) {
        validateQuantity(quantity);

        if (quantity > this.reservedQuantity) {
            throw new IllegalArgumentException("예약된 특가 수량을 초과할 수 없습니다.");
        }

        this.reservedQuantity -= quantity;
        this.soldQuantity += quantity;

        // 완판 체크
        if (this.soldQuantity == this.totalQuantity) {
            this.status = PromotionStatus.SOLD_OUT;
        }
    }

    // 특가 판매 취소 (환불 시)
    public void cancelPromotionSale(int quantity) {
        validateQuantity(quantity);

        if (quantity > this.soldQuantity) {
            throw new IllegalArgumentException("판매된 특가 수량을 초과할 수 없습니다.");
        }

        this.soldQuantity -= quantity;

        // 특가 재고가 다시 생긴 경우 상태 복구
        if (this.status == PromotionStatus.SOLD_OUT && getAvailableQuantity() > 0) {
            this.status = PromotionStatus.ACTIVE;
        }
    }

    // 특가 수량 추가
    public void addPromotionStock(int quantity) {
        validateQuantity(quantity);
        this.totalQuantity += quantity;

        // 특가 재고가 생긴 경우 상태 복구
        if (this.status == PromotionStatus.SOLD_OUT && getAvailableQuantity() > 0) {
            this.status = PromotionStatus.ACTIVE;
        }
    }

    // 특가 수량 차감
    public void reducePromotionStock(int quantity) {
        validateQuantity(quantity);

        // 최소한 예약된 수량 + 판매된 수량은 유지해야 함
        int minimumRequired = this.reservedQuantity + this.soldQuantity;
        if (this.totalQuantity - quantity < minimumRequired) {
            throw new IllegalArgumentException("특가 총 할당량은 최소 " + minimumRequired + "개 이상이어야 합니다.");
        }

        this.totalQuantity -= quantity;

        // 특가 재고 부족 시 상태 변경
        if (getAvailableQuantity() == 0) {
            this.status = PromotionStatus.SOLD_OUT;
        }
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

    // 특가 활성 여부 확인 (active 거나 sold_out, 아직 기한이 끝나지 않음)
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return (this.status == PromotionStatus.ACTIVE || this.status == PromotionStatus.SOLD_OUT)
                && now.isBefore(endAt);
    }
}