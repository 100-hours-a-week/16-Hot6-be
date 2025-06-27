package com.kakaotech.ott.ott.aiImage.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.request.ProductDetailRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiImageAndProductRequestDto {

    @NotNull(message = "initial_image_url은 필수입니다.")
    @JsonProperty("initial_image_url")
    private String initialImageUrl;

    @JsonProperty("processed_image_url")
    private String processedImageUrl;

    private List<ProductDetailRequestDto> products = new ArrayList<>();
}