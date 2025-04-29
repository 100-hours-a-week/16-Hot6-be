package com.kakaotech.ott.ott.global.exception;

import com.kakaotech.ott.ott.global.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .code(errorCode.name())
                .build();

        log.error("[CustomException] {} - {}", errorCode.name(), errorCode.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleServerException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value())
                .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .code(ErrorCode.INTERNAL_SERVER_ERROR.name())
                .build();

        log.error("[Exception] 서버 내부 오류", e);
        return new ResponseEntity<>(errorResponse, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}
