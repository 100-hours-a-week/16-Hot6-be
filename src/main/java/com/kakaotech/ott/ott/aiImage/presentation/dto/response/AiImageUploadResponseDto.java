package com.kakaotech.ott.ott.aiImage.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AiImageUploadResponseDto {

    private Long imageId;

    private int estimatedTimeSec;
}
