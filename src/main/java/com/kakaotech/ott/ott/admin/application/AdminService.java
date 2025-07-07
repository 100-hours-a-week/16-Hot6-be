package com.kakaotech.ott.ott.admin.application;

import com.kakaotech.ott.ott.admin.presentation.dto.request.PromotionCreateRequestDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminDeliveryResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminProductStatusResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminRefundResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.PromotionCreateResponseDto;

public interface AdminService {

    AdminProductStatusResponseDto getOrderProductStatus();

    AdminDeliveryResponseDto deliveryProduct(Long orderItemId);

    AdminRefundResponseDto refundApproveProduct(Long orderItemId);

    AdminRefundResponseDto refundRejectProduct(Long orderItemId);

    PromotionCreateResponseDto createPromotion(Long variantId, PromotionCreateRequestDto promotionCreateRequest);
}
