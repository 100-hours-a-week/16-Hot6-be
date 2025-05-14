package com.kakaotech.ott.ott.post.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCountDto {
    private Long postId;
    private Integer viewCount;
    private Integer scrapCount;
    private Integer likeCount;
}
