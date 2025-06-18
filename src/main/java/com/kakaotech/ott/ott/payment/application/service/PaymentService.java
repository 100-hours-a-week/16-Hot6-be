package com.kakaotech.ott.ott.payment.application.service;

import com.kakaotech.ott.ott.payment.presentation.dto.request.PaymentRequestDto;
import com.kakaotech.ott.ott.payment.presentation.dto.response.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto, Long userId, Long orderId);
}
