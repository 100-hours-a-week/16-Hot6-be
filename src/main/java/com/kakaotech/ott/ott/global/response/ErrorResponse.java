package com.kakaotech.ott.ott.global.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final int status;
    private final String message;
    private final String code;
}
