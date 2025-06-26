package com.kakaotech.ott.ott.comment.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.util.KstDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommentListResponseDto {

    private final List<CommentResponseDto> comments;
    private final PageInfo pageInfo;

    @Getter
    @AllArgsConstructor
    public static class CommentResponseDto {
        private final Long commentId;
        private final String content;
        private final AuthorDto author;

        @JsonProperty("createdAt")
        private KstDateTime createdAt;
        private final boolean isOwner;
    }

    @Getter
    @AllArgsConstructor
    public static class AuthorDto {
        private final String nickname;
        private final String profileImageUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class PageInfo {
        private final int size;
        private final boolean hasNext;
        private final Long lastCommentId;
    }
}
