package com.kakaotech.ott.ott.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaotech.ott.ott.auth.application.JwtService;
import com.kakaotech.ott.ott.user.domain.model.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomOAuth2User customUser = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = customUser.getUserId();

        // JWT 발급
        String accessToken = jwtService.createAccessToken(userId);
        String refreshToken = jwtService.createRefreshToken(userId);

        // ✅ Refresh Token 저장 (DB 또는 Redis)
        jwtService.storeRefreshToken(userId, refreshToken);

        // ✅ Access Token은 응답 body에 담아 프론트로 전달
        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("accessToken", accessToken);

        // ✅ Refresh Token은 HttpOnly, Secure 쿠키로 설정
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/") // 필요한 경우 /auth 등으로 제한 가능
                .maxAge(7 * 24 * 60 * 60) // 예: 7일
                .build();
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // ✅ 응답 구성
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), tokenResponse);
    }
}