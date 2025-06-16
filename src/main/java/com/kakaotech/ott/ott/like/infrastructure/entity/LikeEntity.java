package com.kakaotech.ott.ott.like.infrastructure.entity;

import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "likes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_user_type_target",
                columnNames = {"user_id", "post_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity postEntity;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Like toDomain() {
        return Like.builder()
                .id(this.id)
                .userId(this.userEntity.getId())
                .postId(this.postEntity.getId())
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .build();
    }

    public static LikeEntity from(Like like, UserEntity userEntity, PostEntity postEntity) {

        return LikeEntity.builder()
                .id(like.getId())
                .userEntity(userEntity)
                .postEntity(postEntity)
                .isActive(like.getIsActive())
                .createdAt(like.getCreatedAt())
                .build();
    }
}