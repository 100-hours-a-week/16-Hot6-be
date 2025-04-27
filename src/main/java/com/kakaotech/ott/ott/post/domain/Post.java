package com.kakaotech.ott.ott.post.domain;

import com.kakaotech.ott.ott.post.entity.PostEntity;
import com.kakaotech.ott.ott.postImage.domain.PostImage;
import com.kakaotech.ott.ott.postImage.entity.PostImageEntity;
import com.kakaotech.ott.ott.user.entity.UserEntity;
import lombok.Builder;
import lombok.Getter;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Getter
@Builder
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

    public static PostEntity toEntity(Post post, UserEntity userEntity) {
        return PostEntity.builder()
                .userEntity(userEntity)
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .viewCount(post.getViewCount())
                .build();


    }

}
