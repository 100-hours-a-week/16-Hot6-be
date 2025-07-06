package com.kakaotech.ott.ott.productOrder.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductInfoDto {

    private Long orderId;
    private Long productId;
    private String productName;
    private String imagePath;
    private int quantity;
    private int unitPrice;
    private String itemStatus;
}
