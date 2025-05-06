package com.kakaotech.ott.ott.like.infrastructure.entity;

import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.model.LikeType;
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
                columnNames = {"user_id", "type", "target_id"}
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private LikeType type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Like toDomain() {
        return Like.builder()
                .id(this.id)
                .userId(this.userEntity.getId())
                .type(this.type)
                .targetId(this.targetId)
                .isActive(this.isActive)
                .createdAt(this.createdAt)
                .build();
    }

    public static LikeEntity from(Like like, UserEntity userEntity) {

        return LikeEntity.builder()
                .id(like.getId())
                .userEntity(userEntity)
                .type(like.getType())
                .targetId(like.getTargetId())
                .isActive(like.getIsActive())
                .createdAt(like.getCreatedAt())
                .build();
    }
}