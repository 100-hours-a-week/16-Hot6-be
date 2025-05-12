package com.kakaotech.ott.ott.user.application.serviceImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.user.application.service.UserAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl implements UserAuthService {

    private final UserAuthRepository userAuthRepository;
    private final JwtService jwtService;
    private final KakaoLogoutServiceImpl kakaoLogoutService;

    @Override
    public boolean checkQuota(Long userId) {

        LocalDate today = LocalDate.now();

        // 1. 사용자가 존재하지 않으면 예외 발생 (404)
        UserEntity userEntity = userAuthRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. quota 사용일이 null이면 → 아직 사용 안 함 → 사용 가능
        LocalDate lastGenerated = userEntity.getAiImageGeneratedDate();

        return lastGenerated == null || !today.equals(lastGenerated);

    }

    @Override
    @Transactional
    public void logout(Long userId, HttpServletResponse response, String kakaoAccessToken) {
        try {
            // 서버에서 Refresh Token 삭제 (DB or Redis)
            jwtService.logout(userId);

            // 클라이언트의 Refresh Token 쿠키 만료
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .domain("dev.onthe-top.com") // 환경에 맞게 도메인 설정
                    .path("/")
                    .maxAge(0)  // 즉시 만료
                    .build();
            response.addHeader("Set-Cookie", refreshCookie.toString());

            if (kakaoAccessToken != null) {
                kakaoLogoutService.logoutFromKakao(kakaoAccessToken);
            }

        } catch (Exception e) {
            throw new CustomException(ErrorCode.LOGOUT_FAILED);
        }
    }
}
