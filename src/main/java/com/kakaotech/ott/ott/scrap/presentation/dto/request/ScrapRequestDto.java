package com.kakaotech.ott.ott.scrap.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapRequestDto {

    @NotNull(message = "스크랩 종류는 필수입니다.")
    private ScrapType type;

    @NotNull(message = "어떤 게시글/상품에 해당하는 스크랩인지 입력하세요.")
    @JsonProperty("targetId")
    private Long targetId;
}
