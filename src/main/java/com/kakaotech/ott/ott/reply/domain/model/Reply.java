package com.kakaotech.ott.ott.reply.domain.model;

import com.kakaotech.ott.ott.reply.infrastructure.entity.ReplyEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Reply {

    private final Long id;
    private final Long userId;
    private final Long commentId;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    public Reply(Long id, Long userId, Long commentId, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.commentId = commentId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Reply createReply(Long userId, Long commentId, String content) {
        LocalDateTime now = LocalDateTime.now();
        return Reply.builder()
                .userId(userId)
                .commentId(commentId)
                .content(content)
                .build();
    }

    public ReplyEntity toEntity() {
        return ReplyEntity.builder()
                .id(this.id)
                .userId(this.userId)
                .commentId(this.commentId)
                .content(this.content)
                .build();
    }
}

