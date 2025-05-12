package com.kakaotech.ott.ott.comment.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequestDto {

    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    private String content;
}
