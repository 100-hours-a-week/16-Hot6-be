package com.kakaotech.ott.ott.recommendProduct.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeskProduct {

    private Long id;

    private Long subCategoryId;

    private String productCode;

    private String name;
    private Integer price;
    private String purchasePlace;

    private Integer scrapCount;
    private Integer clickCount;
    private Integer weight;

    private String purchaseUrl;

    private String imagePath;

    public static DeskProduct createDeskProduct(Long subCategoryId, String productCode,
                                                String name, Integer price, String purchasePlace,
                                                String purchaseUrl, String imagePath) {

        return DeskProduct.builder()
                .subCategoryId(subCategoryId)
                .productCode(productCode)
                .name(name)
                .price(price)
                .purchasePlace(purchasePlace)
                .scrapCount(0)
                .clickCount(0)
                .weight(0)
                .purchaseUrl(purchaseUrl)
                .imagePath(imagePath)
                .build();
    }
}
