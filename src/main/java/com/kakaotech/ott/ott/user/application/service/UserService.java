package com.kakaotech.ott.ott.user.application.service;

import com.kakaotech.ott.ott.user.presentation.dto.response.MyDeskImageResponseDto;

import java.time.LocalDateTime;

public interface UserService {

    MyDeskImageResponseDto getMyDeskWithCursor(Long userId, LocalDateTime createdAtCursor, Long lastId, int size);
}
