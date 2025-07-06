package com.kakaotech.ott.ott.orderItem.domain.model;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.product.domain.model.ProductPromotion;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class OrderItem {

    private Long id;

    private Long orderId;

    private Long variantsId;

    private Long promotionId;

    private OrderItemStatus status;

    private int originalPrice;

    private int quantity;

    private int discountAmount;

    private int finalPrice;

    private int refundAmount;

    private RefundReason refundReason;

    private CancelReason cancelReason;

    private LocalDateTime canceledAt;

    private LocalDateTime refundRequestedAt;

    private LocalDateTime refundedAt;

    public static OrderItem createOrderItem(ProductVariant variant, ProductPromotion promotion, Long orderId, int quantity) {

        int unitPrice = variant.getPrice();
        int originalPrice = unitPrice * quantity;
        int discountAmount = 0;
        int finalPrice = originalPrice;

        Long promotionId = null;

        if (promotion != null) {
            int discountPrice = promotion.getDiscountPrice();
            discountAmount = (unitPrice - discountPrice) * quantity;
            finalPrice = discountPrice * quantity;
            promotionId = promotion.getId();
        }

        return OrderItem.builder()
                .orderId(orderId)
                .variantsId(variant.getId())
                .promotionId(promotionId) // 삼항 연산자 사용
                .status(OrderItemStatus.PENDING)
                .originalPrice(originalPrice)
                .quantity(quantity)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void fail() {
        if (this.status != OrderItemStatus.PENDING) throw new CustomException(ErrorCode.NOT_PENDING_STATE);

        this.status = OrderItemStatus.FAILED;
    }

    public void pay() {
        if (this.status != OrderItemStatus.PENDING) throw new CustomException(ErrorCode.NOT_PENDING_STATE);

        this.status = OrderItemStatus.PAID;
    }

    public void deliver() {
        if (this.status != OrderItemStatus.PAID) throw new CustomException(ErrorCode.NOT_PAID_STATE);

        this.status = OrderItemStatus.DELIVERED;
    }

    public void cancel(CancelReason cancelReason, LocalDateTime canceledAt) {
        if (this.status != OrderItemStatus.PAID) throw new CustomException(ErrorCode.NOT_CANCELABLE_STATE);

        this.status = OrderItemStatus.CANCELED;
        this.refundAmount = this.finalPrice;
        this.cancelReason = cancelReason;
        this.canceledAt = canceledAt;
    }

    public void refundRequest(RefundReason refundReason, LocalDateTime refundRequestedAt) {
        if (this.status != OrderItemStatus.DELIVERED) throw new CustomException(ErrorCode.NOT_REFUNDABLE_STATE);

        this.status = OrderItemStatus.REFUND_REQUEST;
        this.refundReason = refundReason;
        this.refundRequestedAt = refundRequestedAt;
    }

    public void refundApprove() {
        if (this.status != OrderItemStatus.REFUND_REQUEST) throw new CustomException(ErrorCode.NOT_REFUNDABLE_STATE);

        this.status = OrderItemStatus.REFUND_APPROVED;
        this.refundAmount = this.finalPrice;
        this.refundedAt = LocalDateTime.now();
    }

    public void refundReject() {
        if (this.status != OrderItemStatus.REFUND_REQUEST) throw new CustomException(ErrorCode.NOT_REFUNDABLE_STATE);

        this.status = OrderItemStatus.REFUND_REJECTED;
    }

    public void confirm() {
        if (this.status != OrderItemStatus.DELIVERED) throw new CustomException(ErrorCode.NOT_CONFIRMABLE_STATE);

        this.status = OrderItemStatus.CONFIRMED;
    }

}
