package com.kakaotech.ott.ott.aiImage.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AiImage {

    private Long id;

    private Long userId;

    private Long postId;

    private AiImageConcept concept;

    private AiImageState state;

    private String beforeImagePath;

    private String afterImagePath;

    private LocalDateTime createdAt;

    public static AiImage createAiImage(Long userId, AiImageConcept concept, String beforeImagePath) {

        return AiImage.builder()
                .userId(userId)
                .postId(null)
                .concept(concept)
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
