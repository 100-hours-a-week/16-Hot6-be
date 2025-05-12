package com.kakaotech.ott.ott.postImage.entity;

import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.postImage.domain.PostImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_images")
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    public PostImage toDomain() {
        return PostImage.builder()
                .id(this.id)
                .postId(this.postEntity.getId())
                .sequence(this.sequence)
                .imageUuid(this.imageUuid)
                .build();
    }
}
