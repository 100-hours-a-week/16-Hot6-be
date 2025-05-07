package com.kakaotech.ott.ott.comment.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    private Long id;
    private Long userId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Comment createComment(Long userId, Long postId, String content) {
        return Comment.builder()
                .userId(userId)
                .postId(postId)
                .content(content)
                .build();
    }

    public void updateContent(String content) {
        if (content != null && !content.isBlank())
            this.content = content;
    }

}