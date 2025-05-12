package com.kakaotech.ott.ott.user.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoUpdateResponseDto {

    private String profileImagePath;

    private String nicknameCommunity;

    private String nicknameKakao;
}
