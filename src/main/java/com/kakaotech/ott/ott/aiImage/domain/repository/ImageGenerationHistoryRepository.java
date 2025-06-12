package com.kakaotech.ott.ott.aiImage.domain.repository;

import java.time.LocalDate;

public interface ImageGenerationHistoryRepository {

    int checkGenerationTokenCount(Long userId, LocalDate dateKey);
}
