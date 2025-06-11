package com.kakaotech.ott.ott.product.domain.model;

public enum VariantStatus {
    ACTIVE,         // 판매 가능
    INACTIVE,       // 일시 판매 중지
    OUT_OF_STOCK,   // 재고 없음
    DISCONTINUED    // 영구 단종
}
