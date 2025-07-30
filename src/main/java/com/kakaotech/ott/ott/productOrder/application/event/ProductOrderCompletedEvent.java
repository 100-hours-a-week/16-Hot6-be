package com.kakaotech.ott.ott.productOrder.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductOrderCompletedEvent {
    private final Long ProductOrderId;
}
