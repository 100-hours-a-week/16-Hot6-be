package com.kakaotech.ott.ott.payment.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.payment.application.service.PaymentService;
import com.kakaotech.ott.ott.payment.presentation.dto.request.PaymentRequestDto;
import com.kakaotech.ott.ott.payment.presentation.dto.response.PaymentResponseDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponseDto>> createPayment(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long orderId,
            @RequestBody @Valid PaymentRequestDto paymentRequestDto) {

        Long userId = userPrincipal.getId();

        PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentRequestDto, userId, orderId);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("상품 결제 성공", paymentResponseDto));
    }
}
