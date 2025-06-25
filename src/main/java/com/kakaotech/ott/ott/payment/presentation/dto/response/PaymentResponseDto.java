package com.kakaotech.ott.ott.payment.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private Long pointHistoryId;
    private Long orderId;
    private int usedPoint;
    private LocalDateTime purchasedAt;
}
