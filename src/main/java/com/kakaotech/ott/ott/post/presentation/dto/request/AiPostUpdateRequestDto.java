package com.kakaotech.ott.ott.reply.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiPostUpdateRequestDto {

    private String title;

    private String content;

    @JsonProperty("ai_image_id")
    private Long aiImageId;
}
