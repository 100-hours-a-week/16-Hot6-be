package com.kakaotech.ott.ott.comment.entity;

import com.kakaotech.ott.ott.comment.domain.Comment;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public Comment toDomain() {
        return Comment.builder()
                .id(this.id)
                .userId(this.userId)
                .postId(this.postId)
                .content(this.content)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }
}
