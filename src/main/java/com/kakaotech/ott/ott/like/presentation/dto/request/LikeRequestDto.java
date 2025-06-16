package com.kakaotech.ott.ott.like.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeRequestDto {

    @NotNull(message = "어떤 게시글/상품에 해당하는 좋아요인지 입력하세요.")
    private Long postId;
}
