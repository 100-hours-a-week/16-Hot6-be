package com.kakaotech.ott.ott.aiImage.application.service;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.AiImageAndProductResponseDto;

public interface AiImageService {

    AiImage createdAiImage(AiImageAndProductRequestDto aiImageAndProductRequestDto, Long userId);

    AiImageAndProductResponseDto getAiImage(Long imageId, Long userId);

}
