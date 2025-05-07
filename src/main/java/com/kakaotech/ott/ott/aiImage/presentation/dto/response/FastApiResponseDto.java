package com.kakaotech.ott.ott.aiImage.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FastApiResponseDto {

    @NotNull(message = "initial_image_url은 필수 항목입니다.")
    @JsonProperty("initial_image_url")
    private String initialImageUrl;

    @NotNull(message = "classify는 필수 항목입니다.")
    private boolean classify;
}
