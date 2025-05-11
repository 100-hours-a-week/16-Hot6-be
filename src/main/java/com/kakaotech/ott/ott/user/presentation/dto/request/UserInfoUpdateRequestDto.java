package com.kakaotech.ott.ott.user.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String nicknameCommunity;

    @JsonProperty("nickname_kakao")
    private String nicknameKakao;
}
