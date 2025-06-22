package com.kakaotech.ott.ott.admin.application;

import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminDeliveryResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminProductStatusResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminRefundResponseDto;

public interface AdminService {

    AdminProductStatusResponseDto getOrderProductStatus();

    AdminDeliveryResponseDto deliveryProduct(Long orderItemId);

    AdminRefundResponseDto refundProduct(Long orderItemId);
}
