package com.kakaotech.ott.ott.post.infrastructure.entity;

import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.postImage.entity.PostImageEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.util.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "type", nullable = false)
    private String type;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "content", nullable = false)
    private String content;

    @OneToMany(mappedBy = "postEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImageEntity> images = new ArrayList<>();

    @Column(name = "comment_count", nullable = false)
    private int commentCount;
    @Column(name = "like_count", nullable = false)
    private int likeCount;
    @Column(name = "view_count", nullable = false)
    private int viewCount;
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
//                .images(this.images != null
//                        ? this.images.stream().map(PostImageEntity::toDomain).toList()
//                        : List.of())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }

}
