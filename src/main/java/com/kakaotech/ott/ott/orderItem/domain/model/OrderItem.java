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

    private OrderItemStatus pendingProductStatus;

    private int refundAmount;

    private RefundReason refundReason;

    private LocalDateTime canceledAt;

    private LocalDateTime refundedAt;

    public static OrderItem createOrderItem(Long orderId, Long variantsId, Long promotionId, int originalPrice, int quantity, int discountAmount, int finalPrice) {

        return OrderItem.builder()
                .orderId(orderId)
                .variantsId(variantsId)
                .promotionId(promotionId)
                .status(OrderItemStatus.PENDING)
                .originalPrice(originalPrice)
                .quantity(quantity)
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .pendingProductStatus(OrderItemStatus.PENDING)
                .build();
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setPendingProductPid() {
        this.pendingProductStatus = null;
    }

    public void pay() {
        if (this.status != OrderItemStatus.PENDING) throw new CustomException(ErrorCode.NOT_PENDING_STATE);

        this.status = OrderItemStatus.PAID;
        this.pendingProductStatus = null;
    }

    public void deliver() {
        if (this.status != OrderItemStatus.PAID) throw new CustomException(ErrorCode.NOT_PAID_STATE);

        this.status = OrderItemStatus.DELIVERED;
    }

    public void cancel(RefundReason refundReason, LocalDateTime canceledAt) {
        if (this.status != OrderItemStatus.PAID) throw new CustomException(ErrorCode.NOT_CANCELABLE_STATE);

        this.status = OrderItemStatus.CANCELED;
        this.refundAmount = this.finalPrice * this.getQuantity();
        this.refundReason = refundReason;
        this.canceledAt = canceledAt;
    }

    public void refund(RefundReason refundReason, LocalDateTime refundedAt) {
        if (this.status != OrderItemStatus.DELIVERED) throw new CustomException(ErrorCode.NOT_REFUNDABLE_STATE);

        this.status = OrderItemStatus.REFUND;
        this.refundAmount = this.finalPrice * this.getQuantity();
        this.refundReason = refundReason;
        this.refundedAt = refundedAt;
    }

    public void confirm() {
        if (this.status != OrderItemStatus.DELIVERED) throw new CustomException(ErrorCode.NOT_CONFIRMABLE_STATE);

        this.status = OrderItemStatus.CONFIRMED;
    }

}
