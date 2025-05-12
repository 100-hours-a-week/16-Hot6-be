package com.kakaotech.ott.ott.user.application.serviceImpl;

import com.kakaotech.ott.ott.user.domain.repository.RefreshTokenRepository;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.RefreshTokenEntity;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKeyPlain;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private Key secretKey;

    private final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 30;       // 30분
    private final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7일

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyPlain.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS_TOKEN_EXPIRATION);
    }

    /**
     * Refresh Token 생성 및 DB 저장
     */
    public String createRefreshToken(Long userId) {
        String refreshToken = createToken(userId, REFRESH_TOKEN_EXPIRATION);
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000);
        storeRefreshToken(userId, refreshToken, expirationDate);
        return refreshToken;
    }

    /**
     * Refresh Token 저장 또는 갱신
     */
    @Transactional
    public void storeRefreshToken(Long userId, String refreshToken, LocalDateTime expirationDate) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findById(userId)
                .orElseGet(() -> RefreshTokenEntity.builder().userId(userId).build());

        tokenEntity.updateRefreshToken(refreshToken, expirationDate);
        refreshTokenRepository.save(tokenEntity);
    }

    /**
     * Refresh Token 만료 시간 갱신 (로그인 시 자동 연장)
     */
    @Transactional
    public void updateRefreshTokenExpiration(String refreshToken) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        LocalDateTime newExpiration = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000);
        tokenEntity.updateRefreshToken(tokenEntity.getRefreshToken(), newExpiration);
        refreshTokenRepository.save(tokenEntity);

        log.info("DB에서 Refresh Token 만료 시간 갱신 완료. 새로운 만료 시간: {}", newExpiration);
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.delete(userId);
    }


    /**
     * Refresh Token 삭제 (로그아웃)
     */
    @Transactional
    public void deleteRefreshTokenByValue(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        log.info("DB에서 Refresh Token 삭제 완료.");
    }

    /**
     * Access Token 재발급
     */
    @Transactional
    public String reissueAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REQUIRED);
        }

        if (!validateToken(refreshToken)) {
            Long userId = extractUserId(refreshToken);
            deleteRefreshTokenByValue(refreshToken);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Long userId = extractUserId(refreshToken);
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        if (!refreshToken.equals(tokenEntity.getRefreshToken())) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // ✅ 새로운 Access Token 생성
        String newAccessToken = createAccessToken(userId);

        // ✅ Refresh Token 만료 시간 자동 연장
        updateRefreshTokenExpiration(refreshToken);

        return newAccessToken;
    }

    /**
     * JWT 유효성 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 사용자 ID 추출
     */
    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * JWT 토큰 생성 (공통 메서드)
     */
    private String createToken(Long userId, long expirationTime) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
