package com.kakaotech.ott.ott.comment.domain;

import com.kakaotech.ott.ott.comment.entity.CommentEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Comment {

    private final Long id;
    private final Long userId;
    private final Long postId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public Comment(Long id, Long userId, Long postId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Comment created(Long userId, Long postId, String content) {
        LocalDateTime now = LocalDateTime.now();
        return Comment.builder()
                .userId(userId)
                .postId(postId)
                .content(content)
                .build();
    }

    public CommentEntity toEntity() {
        return CommentEntity.builder()
                .id(this.id)
                .userId(this.userId)
                .postId(this.postId)
                .content(this.content)
                .build();
    }
}