package com.kakaotech.ott.ott.like.domain.model;

import com.kakaotech.ott.ott.like.infrastructure.entity.LikeEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Like {

    private final Long id;
    private final Long userId;
    private final String type;
    private final Long targetId;
    private final Boolean isActive;
    private final LocalDateTime createdAt;

    @Builder
    public Like(Long id, Long userId, String type, Long targetId, Boolean isActive, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.targetId = targetId;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public static Like createLike(Long userId, String type, Long targetId) {
        return Like.builder()
                .userId(userId)
                .type(type)
                .targetId(targetId)
                .isActive(true)  // 기본 활성화
                .build();
    }

    public LikeEntity toEntity() {
        return LikeEntity.builder()
                .id(this.id)
                .userId(this.userId)
                .type(this.type)
                .targetId(this.targetId)
                .isActive(this.isActive)
                .build();
    }
}
