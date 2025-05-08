package com.kakaotech.ott.ott.reply.infrastructure.entity;

import com.kakaotech.ott.ott.comment.infrastructure.entity.CommentEntity;
import com.kakaotech.ott.ott.reply.domain.model.Reply;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "replies")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReplyEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity commentEntity;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    public Reply toDomain() {
        return Reply.builder()
                .id(this.id)
                .userId(this.userEntity.getId())
                .commentId(this.commentEntity.getId())
                .content(this.content)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static ReplyEntity from(Reply reply, UserEntity userEntity, CommentEntity commentEntity) {
        return ReplyEntity.builder()
                .id(reply.getId())
                .userEntity(userEntity)
                .commentEntity(commentEntity)
                .content(reply.getContent())
                .build();
    }
}
