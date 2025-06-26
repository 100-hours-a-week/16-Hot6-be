package com.kakaotech.ott.ott.payment.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.util.KstDateTime;
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
    @JsonProperty("purchasedAt")
    private KstDateTime purchasedAt;
}
