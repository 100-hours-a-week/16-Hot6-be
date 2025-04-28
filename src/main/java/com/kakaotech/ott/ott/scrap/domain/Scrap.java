package com.kakaotech.ott.ott.scrap.domain;

import com.kakaotech.ott.ott.scrap.entity.ScrapEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class Scrap {

    private final Long id;
    private final Long userId;
    private final String type;
    private final Long targetId;
    private final Boolean isActive;
    private final LocalDateTime createdAt;

    public static Scrap created(Long userId, String type, Long targetId) {
        return Scrap.builder()
                .userId(userId)
                .type(type)
                .targetId(targetId)
                .isActive(true) // 기본 활성화
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ScrapEntity toEntity() {
        return ScrapEntity.builder()
                .id(this.id)
                .userId(this.userId)
                .type(this.type)
                .targetId(this.targetId)
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .build();
    }
}
