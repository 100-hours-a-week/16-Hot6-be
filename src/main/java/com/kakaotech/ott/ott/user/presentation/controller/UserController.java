package com.kakaotech.ott.ott.user.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.user.application.service.UserService;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyDeskImageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me/desks")
    public ResponseEntity<ApiResponse<MyDeskImageResponseDto>> getMyDesk(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam (value = "createdAtCursor", required = false) LocalDateTime createdAtCursor,
            @RequestParam (value = "lastId", required = false) Long lastId,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = userPrincipal.getId();

        // 기본 값 설정
        if (createdAtCursor == null) {
            createdAtCursor = LocalDateTime.now();
        }

        // lastId가 없으면 기본값 (가장 최신 ID) 설정
        if (lastId == null) {
            lastId = Long.MAX_VALUE; // 가장 최신 ID를 의미
        }

        MyDeskImageResponseDto myDeskImageResponseDto = userService.getMyDeskWithCursor(userId, createdAtCursor, lastId, size);

        return ResponseEntity.ok(ApiResponse.success("나의 데스크 조회 성공", myDeskImageResponseDto));
    }
}
