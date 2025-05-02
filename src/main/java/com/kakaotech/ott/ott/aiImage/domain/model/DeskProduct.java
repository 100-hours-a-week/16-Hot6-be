package com.kakaotech.ott.ott.aiImage.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DeskProduct {

    private Long id;

    private Long subCategoryId;
    private Long aiImageId;

    private String name;
    private Integer price;
    private String purchasePlace;

    private Integer scrapCount;
    private Integer clickCount;
    private Integer weight;

    private String purchaseUrl;

    private Integer centerX;
    private Integer centerY;

    private String imagePath;

    @Builder
    public DeskProduct(Long id, Long subCategoryId, Long aiImageId,
                       String name, Integer price, String purchasePlace,
                       Integer scrapCount, Integer clickCount, Integer weight,
                       String purchaseUrl, Integer centerX, Integer centerY, String imagePath) {
        this.id = id;
        this.subCategoryId = subCategoryId;
        this.aiImageId = aiImageId;
        this.name = name;
        this.price = price;
        this.purchasePlace = purchasePlace;
        this.scrapCount = scrapCount;
        this.clickCount = clickCount;
        this.weight = weight;
        this.purchaseUrl = purchaseUrl;
        this.centerX = centerX;
        this.centerY = centerY;
        this.imagePath = imagePath;
    }

    public static DeskProduct createDeskProduct(Long subCategoryId, Long aiImageId,
                                                String name, Integer price, String purchasePlace,
                                                String purchaseUrl, Integer centerX, Integer centerY, String imagePath) {

        return DeskProduct.builder()
                .subCategoryId(subCategoryId)
                .aiImageId(aiImageId)
                .name(name)
                .price(price)
                .purchasePlace(purchasePlace)
                .scrapCount(0)
                .clickCount(0)
                .weight(0)
                .purchaseUrl(purchaseUrl)
                .centerX(centerX)
                .centerY(centerY)
                .imagePath(imagePath)
                .build();
    }
}
