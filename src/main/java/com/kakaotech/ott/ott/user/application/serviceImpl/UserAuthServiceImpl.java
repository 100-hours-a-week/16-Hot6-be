package com.kakaotech.ott.ott.user.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.aiImage.domain.repository.ImageGenerationHistoryRepository;
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
    private final KakaoLogoutServiceImpl kakaoLogoutService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ImageGenerationHistoryRepository imageGenerationHistoryRepository;
    private final AiImageRepository aiImageRepository;

    @Transactional(readOnly = true)
    public CheckAiImageQuotaResponseDto remainQuota(Long userId) {

        if (userId == null)
            throw new CustomException(ErrorCode.AUTH_REQUIRED);

        // 1. 사용자가 존재하지 않으면 예외 발생 (404)
        User user = userAuthRepository.findById(userId);

        int usedToken = aiImageRepository.countUserIdAndStateIn(userId);

        return new CheckAiImageQuotaResponseDto(5-usedToken);
    }

    @Override
    @Transactional(readOnly = true)
    public void checkQuota(Long userId) {

        User user = userAuthRepository.findById(userId);

        int remainToken = imageGenerationHistoryRepository.checkGenerationTokenCount(userId, LocalDate.now());

        if (remainToken == 3)
            throw new CustomException(ErrorCode.QUOTA_ALREADY_USED);

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
