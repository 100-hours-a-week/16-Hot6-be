package com.kakaotech.ott.ott.post.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
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
        private final PostAuthorResponseDto author;
        private final int likeCount;
        private final int commentCount;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private final LocalDateTime createdAt;

        private final boolean liked;
        private final boolean scrapped;
    }


    @Getter
    @AllArgsConstructor
    public static class Pagination {
        private final int size;
        private final Long lastPostId;   // 다음 조회의 커서
        private final boolean hasNext;
    }
}
