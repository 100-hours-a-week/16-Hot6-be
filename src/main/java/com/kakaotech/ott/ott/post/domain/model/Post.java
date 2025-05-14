package com.kakaotech.ott.ott.post.domain.model;

import com.kakaotech.ott.ott.postImage.domain.PostImage;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    private Long id;
    private Long userId;

    private PostType type;
    private String title;
    private String content;

    @Builder.Default
    private List<PostImage> images = new ArrayList<>();

    private int commentCount;
    private int likeCount;

    @Builder.Default
    private Long viewCount = 0L;
    private int scrapCount;

    private double weight;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Post createPost(Long userId, PostType type, String title, String content) {
        return Post.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .content(content)
                .commentCount(0)
                .likeCount(0)
                .viewCount(0L)
                .scrapCount(0)
                .weight(0.0)
                .build();

    }

    // ◀ 수정 메서드
    public void updateTitle(String title) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
    }
    public void updateContent(String content) {
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }

    public void clearImages() { this.images.clear(); }

    public void addImage(PostImage img) { this.images.add(img); }
}
