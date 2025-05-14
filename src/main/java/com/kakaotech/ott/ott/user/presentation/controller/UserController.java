package com.kakaotech.ott.ott.user.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.user.application.service.UserService;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserInfoUpdateRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserVerifiedRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyDeskImageResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyInfoResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.UserInfoUpdateResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
            @RequestParam (value = "cursorId", required = false) Long cursorId,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "type", required = false) String type) {

        Long userId = userPrincipal.getId();

        // lastId가 없으면 기본값 (가장 최신 ID) 설정
        if (cursorId == null) {
            cursorId = Long.MAX_VALUE; // 가장 최신 ID를 의미
        }

        MyDeskImageResponseDto myDeskImageResponseDto = userService.getMyDeskWithCursor(userId, cursorId, size, type);

        return ResponseEntity.ok(ApiResponse.success("나의 데스크 조회 성공", myDeskImageResponseDto));
    }

    @PatchMapping(value = "/me", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<UserInfoUpdateResponseDto>> updateUserInfo(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @ModelAttribute UserInfoUpdateRequestDto userInfoUpdateRequestDto) throws IOException {

        Long userId = userPrincipal.getId();

        UserInfoUpdateResponseDto userInfoUpdateResponseDto = userService.updateUserInfo(userId, userInfoUpdateRequestDto);

        return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 성공", userInfoUpdateResponseDto));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getId();

        userService.deleteUser(userId);

        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다.", null));
    }

    @PostMapping("/recommendation-code")
    public ResponseEntity<ApiResponse> verifiedUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid UserVerifiedRequestDto userVerifiedRequestDto) {

        Long userId = userPrincipal.getId();

        userService.verifiedCode(userId, userVerifiedRequestDto);

        return ResponseEntity.ok(ApiResponse.success("추천인 코드 등록 성공", null));
    }

    @PostMapping("/recover")
    public ResponseEntity<ApiResponse> recoverUserAccount(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal.getId();

        userService.recoverUser(userId);

        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴 취소가 완료되었습니다.", userId));
    }
}
