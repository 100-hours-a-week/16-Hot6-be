package com.kakaotech.ott.ott.reply.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class Reply {

    private Long id;
    private Long userId;
    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static Reply createReply(Long userId, Long commentId, String content) {
        return Reply.builder()
                .userId(userId)
                .commentId(commentId)
                .content(content)
                .build();
    }

    public void updateContent(String content) {

        if(content != null || content.isBlank())
            this.content = content;
    }

}

