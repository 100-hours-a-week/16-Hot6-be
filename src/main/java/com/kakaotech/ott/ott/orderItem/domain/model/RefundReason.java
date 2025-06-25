package com.kakaotech.ott.ott.orderItem.domain.model;

public enum RefundReason {
    CUSTOMER_REQUEST,
    CHANGE_MIND,   // 단순 변심
    OUT_OF_STOCK,       // 재고 부족
    DEFECTIVE_PRODUCT,  // 상품 불량
    WRONG_PRODUCT      // 오배송
}
