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

    private Long postId;

    private Boolean isActive;

    private LocalDateTime createdAt;

    public static Like createLike(Long userId, Long postId) {
        return Like.builder()
                .userId(userId)
                .postId(postId)
                .isActive(true)  // 기본 활성화
                .build();
    }

}
