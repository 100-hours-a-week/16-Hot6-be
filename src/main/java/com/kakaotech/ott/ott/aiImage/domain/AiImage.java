package com.kakaotech.ott.ott.aiImage.domain;

import com.kakaotech.ott.ott.aiImage.entity.AiImageEntity;
import com.kakaotech.ott.ott.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class AiImage {

    private Long id;

    private Long userId;

    private Long postId;

    private String beforeImagePath;

    private String afterImagePath;

    private LocalDateTime createdAt;

    public static AiImage createAiImage(Long userId, Long postId, String beforeImagePath, String afterImagePath) {

        return AiImage.builder()
                .userId(userId)
                .postId(postId)
                .beforeImagePath(beforeImagePath)
                .afterImagePath(afterImagePath)
                .build();
    }

    public AiImageEntity toEntity(UserEntity userEntity) {

        return AiImageEntity.builder()
                .userEntity(userEntity)
                .postId(this.getPostId())
                .beforeImagePath(this.getBeforeImagePath())
                .afterImagePath(this.getAfterImagePath())
                .build();
    }
}
