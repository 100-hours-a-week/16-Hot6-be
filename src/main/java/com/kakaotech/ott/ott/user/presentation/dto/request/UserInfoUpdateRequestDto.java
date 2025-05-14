package com.kakaotech.ott.ott.user.presentation.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoUpdateRequestDto {

    private MultipartFile profileImagePath;

    @Size(min = 2, max = 20, message = "커뮤니티 닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nicknameCommunity;

    @Size(min = 2, max = 20, message = "카카오 닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nicknameKakao;
}
