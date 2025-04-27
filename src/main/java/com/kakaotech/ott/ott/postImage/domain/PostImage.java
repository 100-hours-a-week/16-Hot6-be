package com.kakaotech.ott.ott.postImage.domain;

import com.kakaotech.ott.ott.post.entity.PostEntity;
import com.kakaotech.ott.ott.postImage.entity.PostImageEntity;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostImage {

    private Long id;
    private Long postId;

    private int sequence;

    private String imageUuid;


    // 나중에 유효성 검사 때 필요
    public PostImage(Long id, Long postId, int sequence, String imageUuid) {
        this.id = id;
        this.postId = postId;
        this.sequence = sequence;
        this.imageUuid = imageUuid;
    }

    public PostImageEntity toEntity(PostEntity postEntity) {
        return PostImageEntity.builder()
                .postEntity(postEntity)
                .sequence(this.getSequence())
                .imageUuid(this.getImageUuid())
                .build();
    }
}
