package com.kakaotech.ott.ott.orderItem.domain.model;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
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

    private LocalDateTime canceledAt;

    private LocalDateTime refundRequestedAt;

    private LocalDateTime refundedAt;

    public static OrderItem createOrderItem(Long orderId, Long variantsId, Long promotionId, int originalPrice, int quantity, int discountAmount, int finalPrice) {

        return OrderItem.builder()
                .orderId(orderId)
                .variantsId(variantsId)
                .promotionId(promotionId != null ? promotionId : null) // 삼항 연산자 사용
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

    public void cancel(RefundReason refundReason, LocalDateTime canceledAt) {
        if (this.status != OrderItemStatus.PAID) throw new CustomException(ErrorCode.NOT_CANCELABLE_STATE);

        this.status = OrderItemStatus.CANCELED;
        this.refundAmount = this.finalPrice;
        this.refundReason = refundReason;
        this.canceledAt = canceledAt;
    }

    public void refundRequest(RefundReason refundReason, LocalDateTime refundRequestedAt) {
        if (this.status != OrderItemStatus.DELIVERED) throw new CustomException(ErrorCode.NOT_REFUNDABLE_STATE);

        this.status = OrderItemStatus.REFUND_REQUEST;
        this.refundReason = refundReason;
        this.refundRequestedAt = refundRequestedAt;
    }

    public void refund() {
        if (this.status != OrderItemStatus.REFUND_REQUEST) throw new CustomException(ErrorCode.NOT_REFUNDABLE_STATE);

        this.status = OrderItemStatus.REFUND;
        this.refundAmount = this.finalPrice;
        this.refundedAt = LocalDateTime.now();
    }

    public void confirm() {
        if (this.status != OrderItemStatus.DELIVERED) throw new CustomException(ErrorCode.NOT_CONFIRMABLE_STATE);

        this.status = OrderItemStatus.CONFIRMED;
    }

}
