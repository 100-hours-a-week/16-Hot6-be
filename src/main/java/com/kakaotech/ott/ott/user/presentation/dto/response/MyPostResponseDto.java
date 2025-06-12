package com.kakaotech.ott.ott.user.presentation.dto.response;

import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyPostResponseDto {

    private List<PostAllResponseDto.Posts> posts;
    private MyPostResponseDto.Pagination pagination;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int size;
        private Long lastPostId;
        private boolean hasNext;
    }
}