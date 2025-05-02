package com.kakaotech.ott.ott.aiImage.application.service;

import com.kakaotech.ott.ott.aiImage.presentation.dto.response.FastApiResponseDto;

public interface FastApiClient {

    FastApiResponseDto sendBeforeImageToFastApi(String imageName);
}
