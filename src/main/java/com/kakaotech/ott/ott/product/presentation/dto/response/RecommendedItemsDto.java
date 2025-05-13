package com.kakaotech.ott.ott.product.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedItemsDto {

    private Long itemId;

    private String productName;

    private String imageUrl;

    private String purchaseUrl;

    private String seller;

    private String subCategory;

    private boolean scrapped;
}
