package com.kakaotech.ott.ott.product.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductInventoryUpdateEvent {
    private final Long productOrderId;
}
