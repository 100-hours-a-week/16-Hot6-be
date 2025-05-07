package com.kakaotech.ott.ott.like.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {

    private Long id;
    private Long userId;

    private LikeType type;

    private Long targetId;

    private Boolean isActive;

    private LocalDateTime createdAt;

    public static Like createLike(Long userId, LikeType type, Long targetId) {
        return Like.builder()
                .userId(userId)
                .type(type)
                .targetId(targetId)
                .isActive(true)  // 기본 활성화
                .build();
    }

}
