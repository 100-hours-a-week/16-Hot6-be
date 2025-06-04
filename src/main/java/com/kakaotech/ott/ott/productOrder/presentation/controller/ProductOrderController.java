package com.kakaotech.ott.ott.productOrder.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.productOrder.application.service.ProductOrderService;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.ProductOrderResponseDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class ProductOrderController {

    private final ProductOrderService productOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductOrderResponseDto>> createOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid ProductOrderRequestDto productOrderRequestDto) {

        Long userId = userPrincipal.getId();

        ProductOrderResponseDto productOrderResponseDto = productOrderService.create(productOrderRequestDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("주문 생성 완료", productOrderResponseDto));
    }

}
