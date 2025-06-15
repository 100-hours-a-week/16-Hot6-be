package com.kakaotech.ott.ott.aiImage.presentation.dto.response;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImageState;
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

    private LocalDateTime createdAt;

    public void updateAfterImagePath(String afterImagePath) {
        this.afterImagePath = afterImagePath;
    }
}
