package com.kakaotech.ott.ott.recommendProduct.application.service;

import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.DeskProductDetailResponseDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.DeskProductListResponseDto;

public interface DeskProductService {

    DeskProductListResponseDto getDeskProducts(Long userId, Double lastWeight, Long lastRecommendedProductId, int size);

    DeskProductDetailResponseDto getDeskProduct(Long userId, Long deskProductId);
}
