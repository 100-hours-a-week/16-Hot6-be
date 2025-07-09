package com.kakaotech.ott.ott.payment.application.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentCompletedEvent {

    private final Long orderId;
    private final Long userId;
    private final int usedPoint;

}
