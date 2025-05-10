package com.kakaotech.ott.ott.aiImage.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AiImage {

    private Long id;

    private Long userId;

    private Long postId;

    private AiImageState state;

    private String beforeImagePath;

    private String afterImagePath;

    private LocalDateTime createdAt;

    @Builder
    public AiImage(Long id, Long userId, Long postId, AiImageState state, String beforeImagePath, String afterImagePath, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.state = state;
        this.beforeImagePath = beforeImagePath;
        this.afterImagePath = afterImagePath;
        this.createdAt = createdAt;
    }

    public static AiImage createAiImage(Long userId, String beforeImagePath) {

        return AiImage.builder()
                .userId(userId)
                .postId(null)
                .state(AiImageState.PENDING)
                .beforeImagePath(beforeImagePath)
                .afterImagePath(null)
                .build();
    }

    // 새로 추가 ▶ 게시글 연결(postId) 변경
    public void updatePostId(Long postId) {
        this.postId = postId;
    }

    public void updateAiImage(String afterImagePath) {
        this.afterImagePath = afterImagePath;
    }

    public void successState() {
        this.state = AiImageState.SUCCESS;
    }

    public void failedState() {
        this.state = AiImageState.FAILED;
    }
}
