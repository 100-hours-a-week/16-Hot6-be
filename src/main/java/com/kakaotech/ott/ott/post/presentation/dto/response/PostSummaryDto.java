package com.kakaotech.ott.ott.post.presentation.dto.response;

import com.kakaotech.ott.ott.util.KstDateTime;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostSummaryDto {

    private Long postId;
    private String title;
    private String nickname;
    private String profileImage;
    private String thumbnail;
    private int likeCount;
    private int commentCount;
    private long viewCount;
    private double weight;
    private KstDateTime createdAt;
    private boolean liked;
    private boolean scrapped;

    @QueryProjection
    public PostSummaryDto(Long postId, String title, String nickname, String profileImage,
                          String thumbnail, int likeCount, int commentCount, long viewCount,
                          double weight, LocalDateTime createdAt, Boolean liked, Boolean scrapped) {
        this.postId = postId;
        this.title = title;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.thumbnail = thumbnail;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.weight = weight;
        this.createdAt = new KstDateTime(createdAt);
        this.liked = liked != null && liked;
        this.scrapped = scrapped != null && scrapped;
    }
}

