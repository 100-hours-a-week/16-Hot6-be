package com.kakaotech.ott.ott.recommendProduct.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiImageRecommendedProduct {

    private Long id;

    private Long aiImageId;

    private Long deskProductId;

    private Integer centerX;
    private Integer centerY;

    public static AiImageRecommendedProduct createAiImageRecommendedProduct(Long aiImageId, Long deskProductId, Integer centerX, Integer centerY) {

        return AiImageRecommendedProduct.builder()
                .aiImageId(aiImageId)
                .deskProductId(deskProductId)
                .centerX(centerX)
                .centerY(centerY)
                .build();
    }
}
