package com.kakaotech.ott.ott.postImage.entity;

import com.kakaotech.ott.ott.post.entity.PostEntity;
import com.kakaotech.ott.ott.postImage.domain.PostImage;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "images")
@NoArgsConstructor
@Getter
public class PostImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    @Column(name = "sequence", nullable = false)
    private int sequence;

    @Column(name = "image_uuid", nullable = false)
    private String imageUuid;

    // 나중에 유효성 검사 때 필요
    @Builder
    public PostImageEntity(PostEntity postEntity, int sequence, String imageUuid) {
        this.postEntity = postEntity;
        this.sequence = sequence;
        this.imageUuid = imageUuid;
    }

    public PostImage toDomain() {
        return PostImage.builder()
                .id(this.id)
                .postId(this.postEntity.getId())
                .sequence(this.sequence)
                .imageUuid(this.imageUuid)
                .build();
    }
}
