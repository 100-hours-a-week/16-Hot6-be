package com.kakaotech.ott.ott.orderItem.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class OrderItem {

    private Long id;

    private Long orderId;

    private Long productId;

    private OrderItemStatus status;

    private int price;

    private int quantity;

    private OrderItemStatus pendingProductStatus;

    private int refundAmount;

    private RefundReason refundReason;

    private LocalDateTime refundedAt;

    public static OrderItem createOrderItem(Long orderId, Long productId, int price, int quantity) {

        return OrderItem.builder()
                .orderId(orderId)
                .productId(productId)
                .status(OrderItemStatus.ORDERED)
                .price(price)
                .quantity(quantity)
                .pendingProductStatus(OrderItemStatus.PENDING)
                .build();
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setPendingProductPid() {
        this.pendingProductStatus = null;
    }
}
