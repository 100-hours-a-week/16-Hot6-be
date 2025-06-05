package com.kakaotech.ott.ott.productOrder.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.productOrder.application.service.ProductOrderService;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.*;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<ApiResponse<MyProductOrderHistoryListResponseDto>> getOrderHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(required = false) Long lastOrderId,
            @RequestParam(defaultValue = "5") int size) {

        Long userId = userPrincipal.getId();

        MyProductOrderHistoryListResponseDto myProductOrderHistoryListResponseDto = productOrderService.getProductOrderHistory(userId, lastOrderId, size);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("주문 내역 조회 성공", myProductOrderHistoryListResponseDto));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<MyProductOrderResponseDto>> getOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long orderId) {

        Long userId = userPrincipal.getId();

        MyProductOrderResponseDto myProductOrderResponseDto = productOrderService.getProductOrder(userId, orderId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("주문 정보 조회 성공", myProductOrderResponseDto));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse> deleteOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long orderId) {

        Long userId = userPrincipal.getId();

        productOrderService.deleteProductOrder(userId, orderId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("주문 취소 성공", null));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<ApiResponse<ProductOrderConfirmResponseDto>> confirmOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long orderId) {

        Long userId = userPrincipal.getId();

        ProductOrderConfirmResponseDto productOrderConfirmResponseDto = productOrderService.confirmProductOrder(userId, orderId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("구매 확정 완료", productOrderConfirmResponseDto));
    }

}
