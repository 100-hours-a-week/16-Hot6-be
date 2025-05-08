package com.kakaotech.ott.ott.reply.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ReplyListResponseDto {

    private final List<ReplyResponseDto> replies;
    private final ReplyListResponseDto.PageInfo pageInfo;

    @Getter
    @AllArgsConstructor
    public static class ReplyResponseDto {
        private final Long replyId;
        private final String content;
        private final ReplyListResponseDto.AuthorDto author;
        private final LocalDateTime createdAt;
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
        private final Long lastReplyId;
    }
}
