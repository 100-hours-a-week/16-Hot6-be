package com.kakaotech.ott.ott.productOrder.application.service;

import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderPartialCancelRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderPartialConfirmRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.MyProductOrderHistoryListResponseDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.MyProductOrderResponseDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.ProductOrderConfirmResponseDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.ProductOrderResponseDto;

public interface ProductOrderService {

    ProductOrderResponseDto create(ProductOrderRequestDto productOrderRequestDto, Long userId);

    MyProductOrderHistoryListResponseDto getProductOrderHistory(Long userId, Long lastId, int size);

    MyProductOrderResponseDto getProductOrder(Long userId, Long orderId);

    void deleteProductOrder(Long userId, Long orderId);

    ProductOrderConfirmResponseDto confirmProductOrder(Long userId, Long orderId);

    void partialCancelProductOrder(Long userId, Long orderId, ProductOrderPartialCancelRequestDto productOrderPartialCancelRequestDto);

    void cancelProductOrder(Long userId, Long orderId);
}
