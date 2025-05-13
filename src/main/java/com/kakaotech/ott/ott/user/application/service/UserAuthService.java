package com.kakaotech.ott.ott.user.application.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserAuthService {

    boolean checkQuota(Long userId);

    void logout(Long userId, HttpServletRequest request, HttpServletResponse response, String kakaoAccessToken);
}
