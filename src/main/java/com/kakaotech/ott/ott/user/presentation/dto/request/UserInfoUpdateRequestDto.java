package com.kakaotech.ott.ott.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoUpdateRequestDto {

    @JsonProperty("profile_image_path")
    private String profileImage;

    @JsonProperty("nickname_community")
    @Size(min = 2, max = 20, message = "커뮤니티 닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nicknameCommunity;

    @JsonProperty("nickname_kakao")
    @Size(min = 2, max = 20, message = "카카오 닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nicknameKakao;
}
