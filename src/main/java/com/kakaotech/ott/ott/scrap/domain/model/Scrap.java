package com.kakaotech.ott.ott.scrap.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap {

    private Long id;
    private Long userId;
    private ScrapType type;
    private Long targetId;
    private Boolean isActive;
    private LocalDateTime createdAt;

    public static Scrap createScrap(Long userId, ScrapType type, Long targetId) {
        return Scrap.builder()
                .userId(userId)
                .type(type)
                .targetId(targetId)
                .isActive(true) // 기본 활성화
                .build();
    }

}
