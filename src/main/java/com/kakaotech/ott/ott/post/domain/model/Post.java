package com.kakaotech.ott.ott.post.domain.model;

import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Post {

    private Long id;
    private Long userId;

    private String type;
    private String title;
    private String content;

    //@Builder.Default
    //private List<PostImage> images = new ArrayList<>();

    private int commentCount;
    private int likeCount;
    private int viewCount;
    private int scrapCount;

    private int weight;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public Post(Long id, Long userId, String type, String title, String content,
                int commentCount, int likeCount, int viewCount, int scrapCount, int weight,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.content = content;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.scrapCount = scrapCount;
        this.weight = weight;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Post createPost(Long userId, String type, String title, String content) {
        return Post.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .content(content)
                .commentCount(0)
                .likeCount(0)
                .viewCount(0)
                .scrapCount(0)
                .weight(0)
                .build();

    }

    public PostEntity toEntity(UserEntity userEntity) {
        return PostEntity.builder()
                .userEntity(userEntity)
                .title(this.getTitle())
                .content(this.getContent())
                .commentCount(this.getCommentCount())
                .likeCount(this.getLikeCount())
                .viewCount(this.getViewCount())
                .build();
    }

}
