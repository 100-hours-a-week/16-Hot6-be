package com.kakaotech.ott.ott.recommendProduct.presentation.dto.response;

public interface RecommendedProductProjection {
    Long getProductId();
    String getProductName();
    String getImagePath();
    int getPrice();
    String getPurchaseUrl();
    boolean getIsScrapped();
    Integer getCenterX();
    Integer getCenterY();
    Integer getWeight();
}

