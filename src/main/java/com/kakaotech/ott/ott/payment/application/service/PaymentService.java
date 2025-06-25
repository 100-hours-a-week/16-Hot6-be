package com.kakaotech.ott.ott.payment.application.service;

import com.kakaotech.ott.ott.payment.presentation.dto.request.PaymentRequestDto;
import com.kakaotech.ott.ott.payment.presentation.dto.request.RefundRequestDto;
import com.kakaotech.ott.ott.payment.presentation.dto.response.PaymentResponseDto;
import com.kakaotech.ott.ott.payment.presentation.dto.response.RefundResponseDto;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto, Long userId, Long orderId);

    RefundResponseDto refundPayment(RefundRequestDto refundRequestDto, Long userId, Long orderId);
}
