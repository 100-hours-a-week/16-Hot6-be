package com.kakaotech.ott.ott.admin.presentation.controller;

import com.kakaotech.ott.ott.admin.application.AdminService;
import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminProductStatusResponseDto;
import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<AdminProductStatusResponseDto>> getProductStatus(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getId();

        AdminProductStatusResponseDto adminProductStatusResponseDto = adminService.getOrderProductStatus(userId);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("주문 상품 관리 페이지 요청 성공", adminProductStatusResponseDto));
    }
}
