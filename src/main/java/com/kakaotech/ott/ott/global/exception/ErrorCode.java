package com.kakaotech.ott.ott.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 기존 것들
    ACCESS_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "카카오 access token이 누락되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 카카오 access token입니다."),

    // 추가
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Access Token이 만료되었습니다."),
    ACCESS_TOKEN_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "Access Token 서명이 유효하지 않습니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "Refresh token이 누락되었습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "Refresh Token이 만료되었습니다."),
    REFRESH_TOKEN_INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "Refresh Token 서명이 유효하지 않습니다."),

    USER_DELETED(HttpStatus.FORBIDDEN, "탈퇴한 사용자입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
