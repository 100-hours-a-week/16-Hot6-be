package com.kakaotech.ott.ott.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImageConcept;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostAllResponseDto {
    private final List<Posts> posts;
    private final Pagination pagination;

    @Getter
    @AllArgsConstructor
    public static class Posts {
        private final Long postId;
        private final String title;
        private final AiImageConcept concept;
        private final PostAuthorResponseDto author;
        private final String thumbnailUrl;
        private final Long likeCount;
        private final int commentCount;
        private final Long viewCount;
        private final Double weightCount;

        @JsonProperty("createdAt")
        private final KstDateTime createdAt;

        private final boolean liked;
        private final boolean scrapped;
    }

    @Getter
    @AllArgsConstructor
    public static class PostAuthorResponseDto {
        private final String nickname;
        private final String profileImageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class Pagination {
        private final int size;
        private final Long lastPostId;
        private final Long lastLikeCount;
        private final Long lastViewCount;
        private final Double lastWeightCount;
        private final boolean hasNext;
    }
}
