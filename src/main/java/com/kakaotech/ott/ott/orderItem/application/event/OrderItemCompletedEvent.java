package com.kakaotech.ott.ott.orderItem.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class OrderItemCompletedEvent {
    private final Long productOrderId;
}
