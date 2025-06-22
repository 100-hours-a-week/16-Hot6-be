package com.kakaotech.ott.ott.productOrder.presentation.dto.request;

import com.kakaotech.ott.ott.orderItem.domain.model.CancelReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderCancelRequestDto {

    private CancelReason cancelReason;
}
