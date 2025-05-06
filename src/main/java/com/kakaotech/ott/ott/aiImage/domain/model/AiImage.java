package com.kakaotech.ott.ott.aiImage.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiImage {

    private Long id;

    private Long userId;

    private Long postId;

    private String beforeImagePath;

    private String afterImagePath;

    private LocalDateTime createdAt;

    @Builder
    public AiImage(Long id, Long userId, Long postId, String beforeImagePath, String afterImagePath, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.beforeImagePath = beforeImagePath;
        this.afterImagePath = afterImagePath;
        this.createdAt = createdAt;
    }

    public static AiImage createAiImage(Long userId, String beforeImagePath, String afterImagePath) {

        return AiImage.builder()
                .userId(userId)
                .postId(null)
                .beforeImagePath(beforeImagePath)
                .afterImagePath(afterImagePath)
                .build();
    }

    // 새로 추가 ▶ 게시글 연결(postId) 변경
    public void updatePostId(Long postId) {
        this.postId = postId;
    }
}
