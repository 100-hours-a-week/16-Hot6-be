package com.kakaotech.ott.ott.comment.infrastructure.entity;

import com.kakaotech.ott.ott.comment.domain.model.Comment;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommentEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity postEntity;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public Comment toDomain() {
        return Comment.builder()
                .id(this.id)
                .userId(this.userEntity.getId())
                .postId(this.postEntity.getId())
                .content(this.content)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static CommentEntity from(Comment comment, UserEntity userEntity, PostEntity postEntity) {

        return CommentEntity.builder()
                .id(comment.getId())
                .userEntity(userEntity)
                .postEntity(postEntity)
                .content(comment.getContent())
                .build();
    }
}
