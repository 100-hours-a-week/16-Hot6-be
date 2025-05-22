package com.kakaotech.ott.ott.user.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.presentation.dto.response.CheckAiImageQuotaResponseDto;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.RefreshTokenRepository;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import com.kakaotech.ott.ott.user.application.service.UserAuthService;
import com.kakaotech.ott.ott.user.infrastructure.entity.RefreshTokenEntity;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final RefreshTokenRepository refreshTokenRepository;

    public CheckAiImageQuotaResponseDto remainQuota(Long userId) {

        LocalDate today = LocalDate.now();

        // 1. 사용자가 존재하지 않으면 예외 발생 (404)
        User user = userAuthRepository.findById(userId);

        LocalDate lastGenerated = user.getAiImageGeneratedDate();

        if(today.equals(lastGenerated) || lastGenerated == null)
            return new CheckAiImageQuotaResponseDto(0);

        //나중에는 이미지 생성 내역 테이블 불러와서 남은 횟수 반환(현재는 1개)
        return new CheckAiImageQuotaResponseDto(1);
    }

    @Override
    public boolean checkQuota(Long userId) {

        LocalDate today = LocalDate.now();

        // 1. 사용자가 존재하지 않으면 예외 발생 (404)
        User user = userAuthRepository.findById(userId);

        LocalDate lastGenerated = user.getAiImageGeneratedDate();

        if(today.equals(lastGenerated) || lastGenerated == null)
            throw new CustomException(ErrorCode.QUOTA_ALREADY_USED);


        return lastGenerated == null || !today.equals(lastGenerated);

    }

    @Override
    @Transactional
    public void logout(Long userId, HttpServletRequest request, HttpServletResponse response, String kakaoAccessToken) {

        RefreshTokenEntity refreshTokenEntity = refreshTokenRepository.findByRefreshToken(getRefreshTokenFromCookie(request))
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        refreshTokenEntity.updateRefreshToken(null, null);

        try {
            // ✅ Refresh Token 쿠키에서 추출
            String refreshToken = getRefreshTokenFromCookie(request);
            if (refreshToken == null) {
                throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }

            // 클라이언트의 Refresh Token 쿠키 만료
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .domain(".onthe-top.com") // 환경에 맞게 도메인 설정
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

    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
