package com.kakaotech.ott.ott.product.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private Long productId;

    private String productName;

    private String imagePath;

    private int price;

    private String purchaseLink;

    private boolean isScraped;

    // 버전 업그레이드 때 진행
//    private Integer centerX;
//
//    private Integer centerY;

    private double weight;

}
