package com.kakaotech.ott.ott.productOrder.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductDto {

    @NotNull
    private Long productId;

    @NotNull
    private Long promotionId;

    @NotNull
    private int originalPrice;

    @NotNull
    private int quantity;

    @NotNull
    private int discountPrice;

}

// 주문하기
//{
//        "products":
//        [
//        {
//        "productId": 1,
//        "promotionId": 1,
//        "originalPrice": 10000,
//        "quantity": 1,
//        "discountPrice": 5000
//        }
//        ]
//
//        }