package com.kakaotech.ott.ott.productOrder.presentation.dto.response;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrderConfirmResponseDto {

    private Long orderId;
    private ProductOrderStatus status;
}
