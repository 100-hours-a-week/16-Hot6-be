package com.kakaotech.ott.ott.postImage.domain;

import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.postImage.entity.PostImageEntity;
import lombok.Builder;
import lombok.Getter;


@Getter
public class PostImage {

    private Long id;
    private Long postId;

    private int sequence;

    private String imageUuid;


    @Builder
    public PostImage(Long id, Long postId, int sequence, String imageUuid) {
        this.id = id;
        this.postId = postId;
        this.sequence = sequence;
        this.imageUuid = imageUuid;
    }

    public static PostImage createPostImage(Long postId, int sequence, String imageUuid) {
        return PostImage.builder()
                .postId(postId)
                .sequence(sequence)
                .imageUuid(imageUuid)
                .build();
    }

    public PostImageEntity toEntity(PostEntity postEntity) {
        return PostImageEntity.builder()
                .postEntity(postEntity)
                .sequence(this.getSequence())
                .imageUuid(this.getImageUuid())
                .build();
    }
}
