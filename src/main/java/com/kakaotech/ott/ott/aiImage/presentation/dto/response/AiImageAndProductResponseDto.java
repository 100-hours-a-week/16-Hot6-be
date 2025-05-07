package com.kakaotech.ott.ott.aiImage.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiImageAndProductResponseDto {

    private AiImageResponseDto aiImageResponseDto;

    private List<ProductResponseDto> products;

    private boolean hasNext;

    private String nextCursor;
}
