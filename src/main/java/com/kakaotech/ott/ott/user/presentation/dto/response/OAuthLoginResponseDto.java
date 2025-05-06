package com.kakaotech.ott.ott.user.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OAuthLoginResponseDto {

    private String accessToken;
    private UserResponseDto user;

}
