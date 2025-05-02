package com.kakaotech.ott.ott.aiImage.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AiImageAndProductRequestDto {

    @NotNull(message = "initial_image_url은 필수입니다.")
    @JsonProperty("initial_image_url")
    private String initialImageUrl;

    @NotNull(message = "processed_image_url은 필수입니다.")
    @JsonProperty("processed_image_url")
    private String processedImageUrl;

    @NotNull(message = "products는 필수입니다.")
    private List<ProductDetailRequestDto> products;
}
