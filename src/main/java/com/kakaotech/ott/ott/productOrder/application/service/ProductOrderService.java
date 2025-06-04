package com.kakaotech.ott.ott.productOrder.application.service;

import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.ProductOrderResponseDto;

public interface ProductOrderService {

    ProductOrderResponseDto create(ProductOrderRequestDto productOrderRequestDto, Long userId);
}
