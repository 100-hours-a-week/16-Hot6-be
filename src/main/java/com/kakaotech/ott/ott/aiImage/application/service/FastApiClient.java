package com.kakaotech.ott.ott.aiImage.application.service;

import com.kakaotech.ott.ott.aiImage.presentation.dto.request.FastApiRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.FastApiResponseDto;

public interface FastApiClient {

    FastApiResponseDto sendBeforeImageToFastApi(FastApiRequestDto fastApiRequestDto);
}
