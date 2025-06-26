package com.kakaotech.ott.ott.aiImage.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImageState;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiImageResponseDto {

    private Long imageId;

    private Long postId;

    private AiImageState state;

    private String beforeImagePath;

    private String afterImagePath;

    @JsonProperty("createdAt")
    private KstDateTime createdAt;

    public void updateAfterImagePath(String afterImagePath) {
        this.afterImagePath = afterImagePath;
    }
}
