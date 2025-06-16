package com.kakaotech.ott.ott.post.presentation.dto.response;

import com.kakaotech.ott.ott.post.domain.model.PostType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostGetResponseDto {

    private Long postId;

    private String title;

    private String content;

    private PostType type;

    private PostAuthorResponseDto author;

    private long likeCount;

    private int commentCount;

    private long viewCount;

    private boolean scrapped;

    private boolean liked;

    private boolean isOwner;

    private List<?> imageUrls;

    private LocalDateTime createdAt;
}
