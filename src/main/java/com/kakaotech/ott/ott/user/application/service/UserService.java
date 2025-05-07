package com.kakaotech.ott.ott.user.application.service;

import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

    boolean checkQuota(Long userId);

    void logout(Long userId, HttpServletResponse response, String kakaoAccessToken);
}
