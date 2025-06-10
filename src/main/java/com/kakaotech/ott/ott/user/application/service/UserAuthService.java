package com.kakaotech.ott.ott.user.application.service;

import com.kakaotech.ott.ott.aiImage.presentation.dto.response.CheckAiImageQuotaResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserAuthService {

    CheckAiImageQuotaResponseDto remainQuota(Long userId);

    void checkQuota(Long userId);

    void logout(Long userId, HttpServletRequest request, HttpServletResponse response, String kakaoAccessToken);
}
