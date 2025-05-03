package com.kakaotech.ott.ott.aiImage.application.service;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.FastApiRequestDto;
import com.kakaotech.ott.ott.aiImage.presentation.dto.response.AiImageAndProductResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AiImageService {

    boolean handleImageValidation(MultipartFile image) throws IOException;

    AiImage createdAiImage(AiImageAndProductRequestDto aiImageAndProductRequestDto, Long userId);

    AiImageAndProductResponseDto getAiImage(Long imageId, Long userId);

}
