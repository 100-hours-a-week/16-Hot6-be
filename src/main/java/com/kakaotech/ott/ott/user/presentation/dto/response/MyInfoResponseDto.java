package com.kakaotech.ott.ott.user.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyInfoResponseDto {

    private String nicknameCommunity;

    private String nicknameKakao;

    private String profileImagePath;

    private int point;

    private boolean isCertified;
}
