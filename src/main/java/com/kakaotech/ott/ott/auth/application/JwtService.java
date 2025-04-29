package com.kakaotech.ott.ott.auth.application;

import com.kakaotech.ott.ott.auth.domain.RefreshTokenRepository;
import com.kakaotech.ott.ott.auth.infrastructure.RefreshTokenEntity;
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

        // 만료시간 계산
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000);

        // DB에 저장 (없으면 생성, 있으면 업데이트)
        // JwtService 내부
        refreshTokenRepository.findById(userId)
                .ifPresentOrElse(
                        entity -> {
                            entity.updateRefreshToken(refreshToken, expirationDate); // ✅ entity 메서드 호출
                            refreshTokenRepository.save(entity);
                        },
                        () -> {
                            refreshTokenRepository.save(
                                    RefreshTokenEntity.builder()
                                            .userId(userId)
                                            .refreshToken(refreshToken)
                                            .refreshTokenExpiration(expirationDate)
                                            .build()
                            );
                        }
                );

        return refreshToken;
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

    public Long extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    @Transactional
    public void storeRefreshToken(Long userId, String refreshToken) {
        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000);

        refreshTokenRepository.findById(userId)
                .ifPresentOrElse(
                        entity -> {
                            entity.updateRefreshToken(refreshToken, expirationDate);
                            refreshTokenRepository.save(entity);
                        },
                        () -> {
                            refreshTokenRepository.save(
                                    RefreshTokenEntity.builder()
                                            .userId(userId)
                                            .refreshToken(refreshToken)
                                            .refreshTokenExpiration(expirationDate)
                                            .build()
                            );
                        }
                );
    }

    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.findById(userId)
                .ifPresent(entity -> {
                    entity.updateRefreshToken(null, null); // ❗ refreshToken, 만료일 둘 다 null로
                    refreshTokenRepository.save(entity);
                });
    }

    @Transactional
    public String reissueAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_REQUIRED);
        }

        if (!validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        Long userId = extractUserId(refreshToken);

        RefreshTokenEntity tokenEntity = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ACCESS_TOKEN));

        if (!refreshToken.equals(tokenEntity.getRefreshToken())) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        return createAccessToken(userId); // 새로운 AccessToken 발급
    }



}
