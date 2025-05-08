package com.kakaotech.ott.ott.reply.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplyCreateRequestDto {

    @NotNull(message = "답글 내용은 필수입니다.")
    private String content;
}
