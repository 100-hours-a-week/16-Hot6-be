package com.kakaotech.ott.ott.recommendProduct.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeskProductDetailResponseDto {

    private Long productId;

    private String productName;

    private int price;

    private String subCategory;

    private String purchasePlace;

    private String imageUrl;

    private String purchaseUrl;

    private boolean scrapped;
}
