package com.kakaotech.ott.ott.user.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.user.application.service.UserService;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserInfoUpdateRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyDeskImageResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyInfoResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.UserInfoUpdateResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponseDto>> getMyInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getId();

        MyInfoResponseDto myInfoResponseDto = userService.getMyInfo(userId);

        return ResponseEntity.ok(ApiResponse.success("회원정보 조회 성공", myInfoResponseDto));
    }

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

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserInfoUpdateResponseDto>> updateUserInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid UserInfoUpdateRequestDto userInfoUpdateRequestDto) {

        Long userId = userPrincipal.getId();

        UserInfoUpdateResponseDto userInfoUpdateResponseDto = userService.updateUserInfo(userId, userInfoUpdateRequestDto);

        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 성공", userInfoUpdateResponseDto));
    }
}
