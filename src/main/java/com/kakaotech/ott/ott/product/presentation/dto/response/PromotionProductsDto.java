package com.kakaotech.ott.ott.product.presentation.dto.response;

import com.kakaotech.ott.ott.product.domain.model.PromotionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionProductsDto {

    private Long productId;

    private String productName;

    private String imageUuid;

    private int price;

    private PromotionType promotionType;

    private boolean scrapped;
}
