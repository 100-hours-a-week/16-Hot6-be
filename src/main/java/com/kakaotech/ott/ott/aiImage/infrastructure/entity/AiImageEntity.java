package com.kakaotech.ott.ott.aiImage.infrastructure.entity;

import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImageState;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ai_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AiImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK 컬럼명
    private UserEntity userEntity;

    @Column(name = "post_id")
    private Long postId;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 30)
    private AiImageState state;

    @Column(name = "before_image_path", nullable = false)
    private String beforeImagePath;

    @Column(name = "after_image_path")
    private String afterImagePath;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AiImage toDomain() {

        return AiImage.builder()
                .id(this.id)
                .userId(this.userEntity.getId())
                .postId(this.postId)
                .state(this.state)
                .beforeImagePath(this.beforeImagePath)
                .afterImagePath(this.afterImagePath)
                .createdAt(this.createdAt)
                .build();
    }

    public static AiImageEntity from(AiImage aiImage, UserEntity userEntity) {

        return AiImageEntity.builder()
                .userEntity(userEntity)
                .postId(aiImage.getPostId())
                .state(aiImage.getState())
                .beforeImagePath(aiImage.getBeforeImagePath())
                .afterImagePath(aiImage.getAfterImagePath())
                .build();
    }

}
