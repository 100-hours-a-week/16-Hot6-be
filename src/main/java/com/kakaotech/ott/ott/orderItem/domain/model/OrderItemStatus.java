package com.kakaotech.ott.ott.orderItem.domain.model;

public enum OrderItemStatus {
    FAILED,
    PENDING,
    PAID,
    DELIVERED,
    REFUND_REQUEST,
    REFUND,
    CANCELED,
    CONFIRMED
}