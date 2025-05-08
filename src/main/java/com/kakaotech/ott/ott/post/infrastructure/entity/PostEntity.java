package com.kakaotech.ott.ott.reply.infrastructure.entity;

import com.kakaotech.ott.ott.reply.domain.model.Post;
import com.kakaotech.ott.ott.reply.domain.model.PostType;
import com.kakaotech.ott.ott.postImage.domain.PostImage;
import com.kakaotech.ott.ott.postImage.entity.PostImageEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "posts")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK 설정 (Post.user → User.id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // FK 컬럼명
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private PostType type;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false)
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImageEntity> postImages = new ArrayList<>();

    @Column(name = "comment_count", nullable = false)
    private int commentCount;
    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;
    @Column(name = "scrap_count", nullable = false)
    private int scrapCount;

    @Column(name = "weight", nullable = false)
    private int weight;

    public Post toDomain() {
        return Post.builder()
                .id(this.id)
                .userId(this.userEntity.getId())
                .type(this.type)
                .title(this.title)
                .content(this.content)
                .commentCount(this.commentCount)
                .likeCount(this.likeCount)
                .viewCount(this.viewCount)
                .scrapCount(this.scrapCount)
                .weight(this.weight)
                .images(this.postImages != null
                        ? this.postImages.stream().map(PostImageEntity::toDomain).collect(Collectors.toList())
                        : List.of())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

    public static PostEntity from(Post post, UserEntity userEntity) {

        // 1) 기본 필드만 세팅한 PostEntity 생성 (images는 빈 리스트)
        PostEntity entity = PostEntity.builder()
                .userEntity(userEntity)
                .type(post.getType())
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .scrapCount(post.getScrapCount())
                .weight(post.getWeight())
                .build();

        // 2) images 리스트가 null이 아닐 때만 변환 & 추가
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            for (PostImage domainImg : post.getImages()) {
                PostImageEntity imgEntity = domainImg.toEntity(entity);
                entity.getPostImages().add(imgEntity);
            }
        }
        // (없으면 빈 리스트 상태 그대로)

        return entity;
    }

}
