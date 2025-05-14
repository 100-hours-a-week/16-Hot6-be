package com.kakaotech.ott.ott.like.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.like.domain.model.LikeType;
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

    @NotNull(message = "좋아요 종류는 필수입니다.")
    private LikeType type;

    @NotNull(message = "어떤 게시글/상품에 해당하는 좋아요인지 입력하세요.")
    private Long targetId;
}
