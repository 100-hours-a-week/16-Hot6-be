package com.kakaotech.ott.ott.admin.presentation.controller;

import com.kakaotech.ott.ott.admin.application.AdminService;
import com.kakaotech.ott.ott.admin.presentation.dto.request.PromotionCreateRequestDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminDeliveryResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminProductStatusResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminRefundResponseDto;
import com.kakaotech.ott.ott.admin.presentation.dto.response.PromotionCreateResponseDto;
import com.kakaotech.ott.ott.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<AdminProductStatusResponseDto>> getProductStatus() {

        AdminProductStatusResponseDto adminProductStatusResponseDto = adminService.getOrderProductStatus();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("주문 상품 관리 페이지 요청 성공", adminProductStatusResponseDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/orders/{orderItemId}/delivery")
    public ResponseEntity<ApiResponse<AdminDeliveryResponseDto>> delivery (
            @PathVariable Long orderItemId) {

        AdminDeliveryResponseDto adminDeliveryResponseDto = adminService.deliveryProduct(orderItemId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("상품 배달 완료", adminDeliveryResponseDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/orders/{orderItemId}/refund/approve")
    public ResponseEntity<ApiResponse<AdminRefundResponseDto>> refundApprove (
            @PathVariable Long orderItemId) {

        AdminRefundResponseDto adminRefundResponseDto = adminService.refundApproveProduct(orderItemId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("상품 환불 완료", adminRefundResponseDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/orders/{orderItemId}/refund/reject")
    public ResponseEntity<ApiResponse<AdminRefundResponseDto>> refundReject (
            @PathVariable Long orderItemId) {

        AdminRefundResponseDto adminRefundResponseDto = adminService.refundRejectProduct(orderItemId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("상품 환불 거부 완료", adminRefundResponseDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/variants/{variantId}")
    public ResponseEntity<ApiResponse<PromotionCreateResponseDto>> createPromotion (
            @PathVariable Long variantId, @Valid @RequestBody PromotionCreateRequestDto request) {
        PromotionCreateResponseDto promotionCreateResponseDto = adminService.createPromotion(variantId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("특가 정보 등록 완료", promotionCreateResponseDto));
    }
}
