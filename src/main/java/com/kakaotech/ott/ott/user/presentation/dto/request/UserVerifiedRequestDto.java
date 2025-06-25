package com.kakaotech.ott.ott.user.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserVerifiedRequestDto {

    @NotBlank(message = "추천인 코드를 입력하세요.")
    private String code;

    @NotBlank(message = "카테부 닉네임을 입력하세요.")
    private String nicknameKakao;
}
