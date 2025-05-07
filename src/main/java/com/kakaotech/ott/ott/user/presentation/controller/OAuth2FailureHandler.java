package com.kakaotech.ott.ott.user.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.global.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.error("[OAuth2 로그인 실패] {}", exception.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus().value())  // 401
                .message(ErrorCode.INVALID_ACCESS_TOKEN.getMessage())
                .code(ErrorCode.INVALID_ACCESS_TOKEN.name())
                .build();

        response.setStatus(ErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
