package com.kakaotech.ott.ott.aiImage.application.service;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.AiImageAndProductResponseDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.AiImageSaveResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AiImageService {

    AiImageSaveResponseDto handleImageValidation(MultipartFile image, Long userId) throws IOException;

    AiImage insertAiImage(AiImageAndProductRequestDto aiImageAndProductRequestDto);

    AiImageAndProductResponseDto getAiImage(Long imageId, Long userId);

}
