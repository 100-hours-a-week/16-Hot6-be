package com.kakaotech.ott.ott.user.application.service;

import com.kakaotech.ott.ott.user.presentation.dto.request.UserInfoUpdateRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserVerifiedRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyDeskImageResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyInfoResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.UserInfoUpdateResponseDto;

import java.time.LocalDateTime;

public interface UserService {

    MyInfoResponseDto getMyInfo(Long userId);

    MyDeskImageResponseDto getMyDeskWithCursor(Long userId, LocalDateTime createdAtCursor, Long lastId, int size);

    UserInfoUpdateResponseDto updateUserInfo(Long userId, UserInfoUpdateRequestDto userInfoUpdateRequestDto);

    void deleteUser(Long userId);

    void verifiedCode(Long userId, UserVerifiedRequestDto userVerifiedRequestDto);
}
