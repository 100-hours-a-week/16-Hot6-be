package com.kakaotech.ott.ott.admin.application;

import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminProductStatusResponseDto;

public interface AdminService {

    AdminProductStatusResponseDto getOrderProductStatus(Long userId);
}
