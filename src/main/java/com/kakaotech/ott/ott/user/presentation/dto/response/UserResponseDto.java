package com.kakaotech.ott.ott.user.presentation.dto.response;

import com.kakaotech.ott.ott.user.domain.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String nicknameCommunity;
    private String profileImagePath;
    private Role role;

}
