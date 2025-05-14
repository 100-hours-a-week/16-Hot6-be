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

    public String createAccessToken(Long userId) {
        return createToken(userId, ACCESS_TOKEN_EXPIRATION);
    }

    public String createRefreshToken(Long userId) {
        String refreshToken = createToken(userId, REFRESH_TOKEN_EXPIRATION);
        storeRefreshToken(userId, refreshToken);
        return refreshToken;
    }

    @Transactional
    public void storeRefreshToken(Long userId, String refreshToken) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findById(userId)
                .orElseGet(() -> RefreshTokenEntity.builder().userId(userId).build());

        tokenEntity.updateRefreshToken(refreshToken, LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(tokenEntity);
    }

    @Transactional
    public String reissueAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            invalidateRefreshToken(refreshToken);
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Long userId = extractUserId(refreshToken);
        String newAccessToken = createAccessToken(userId);

        return newAccessToken;
    }

    @Transactional
    public void deleteRefreshTokenByValue(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
        log.info("DB에서 Refresh Token 삭제 완료: {}", refreshToken);
    }

    @Transactional
    public void updateRefreshTokenExpiration(String refreshToken) {
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        tokenEntity.updateRefreshToken(tokenEntity.getRefreshToken(), LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(tokenEntity);
    }

    @Transactional
    public void invalidateRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        return Long.parseLong(claims.getSubject());
    }

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
