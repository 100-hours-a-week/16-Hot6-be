package com.kakaotech.ott.ott.aiImage.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ImageGenerationHistory {

    private Long id;

    private Long userId;

    private LocalDate dateKey;

    private AiImageConcept promptSummary;

    private LocalDateTime generatedAt;

    public static ImageGenerationHistory createImageGenerationHisotry(Long userId, LocalDate dateKey, AiImageConcept concept) {

        return ImageGenerationHistory.builder()
                .userId(userId)
                .dateKey(dateKey)
                .promptSummary(concept)
                .build();
    }
}
