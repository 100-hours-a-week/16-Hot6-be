package com.kakaotech.ott.ott.post.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiPostCreateRequestDto {

@NotBlank(message = "게시글 제목은 필수입니다.")
@Size(min = 2, max = 35, message = "게시글 제목은 2자 이상 35자 이하로 입력해주세요.")
private String title;

@NotBlank(message = "게시글 내용은 필수입니다.")
private String content;

@NotNull(message = "데스크 셋업 이미지는 필수입니다.")
@JsonProperty("ai_image_id")
private Long aiImageId;
}
