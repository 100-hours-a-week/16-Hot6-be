package com.kakaotech.ott.ott.productOrder.domain.model;

public enum ProductOrderStatus {
    PENDING,
    PAID,
    CONFIRMED,
    DELIVERED,
    CANCELED,
    PARTIALLY_CANCELED,
    PARTIALLY_REFUNDED,
    REFUNDED
}