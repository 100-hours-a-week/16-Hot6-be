package com.kakaotech.ott.ott.user.application.service;

import com.kakaotech.ott.ott.user.presentation.dto.request.UserInfoUpdateRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.request.UserVerifiedRequestDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyDeskImageResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.MyInfoResponseDto;
import com.kakaotech.ott.ott.user.presentation.dto.response.UserInfoUpdateResponseDto;

import java.io.IOException;

public interface UserService {

    MyInfoResponseDto getMyInfo(Long userId);

    MyDeskImageResponseDto getMyDeskWithCursor(Long userId, Long lastId, int size, String type);

    UserInfoUpdateResponseDto updateUserInfo(Long userId, UserInfoUpdateRequestDto userInfoUpdateRequestDto) throws IOException;

    void deleteUser(Long userId);

    void verifiedCode(Long userId, UserVerifiedRequestDto userVerifiedRequestDto);

    void recoverUser(Long userId);
}
