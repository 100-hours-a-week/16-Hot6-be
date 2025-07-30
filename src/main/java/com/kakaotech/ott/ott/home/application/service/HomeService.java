package com.kakaotech.ott.ott.home.application.service;

import com.kakaotech.ott.ott.home.presentation.dto.response.MainResponseDto;

public interface HomeService {
    MainResponseDto getMainPageData(Long userId);
}
