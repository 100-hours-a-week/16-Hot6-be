package com.kakaotech.ott.ott.global.security;

import com.kakaotech.ott.ott.user.application.serviceImpl.JwtService;
import com.kakaotech.ott.ott.user.application.serviceImpl.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final List<String> publicUrls; // ✅ SecurityConfig에서 주입

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        // ✅ 인증이 필요하지 않은 URL은 필터를 통과
        if (isPublicUrl(requestURI, method)) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = resolveToken(request);
        String refreshToken = getRefreshTokenFromCookie(request);

        try {
            if (accessToken != null && jwtService.validateToken(accessToken)) {
                // ✅ 유효한 Access Token이면 인증 설정
                setAuthentication(accessToken, request);
            } else if (refreshToken != null && jwtService.validateToken(refreshToken)) {
                // ✅ Access Token 만료 -> Refresh Token으로 재발급
                String newAccessToken = jwtService.reissueAccessToken(refreshToken);
                setAuthentication(newAccessToken, request);
                jwtService.updateRefreshTokenExpiration(refreshToken); // ✅ DB에서 만료 시간 갱신
            } else {
                clearRefreshToken(response);
                jwtService.deleteRefreshTokenByValue(refreshToken); // ✅ DB에서 Refresh Token 삭제
            }
        } catch (ExpiredJwtException ex) {
            System.out.println("JWT 인증 필터 - Access Token 만료");
            if (refreshToken != null && jwtService.validateToken(refreshToken)) {
                String newAccessToken = jwtService.reissueAccessToken(refreshToken);
                setAuthentication(newAccessToken, request);
                jwtService.updateRefreshTokenExpiration(refreshToken); // ✅ DB에서 만료 시간 갱신
            } else {
                clearRefreshToken(response);
                jwtService.deleteRefreshTokenByValue(refreshToken); // ✅ DB에서 Refresh Token 삭제
            }
        } catch (JwtException ex) {
            System.out.println("JWT 인증 필터 - JWT 검증 실패");
            clearRefreshToken(response);
            jwtService.deleteRefreshTokenByValue(refreshToken); // ✅ DB에서 Refresh Token 삭제
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicUrl(String requestURI, String method) {
        // ✅ GET 요청에 대한 공용 URL
        if (method.equals("GET") && publicUrls.stream().anyMatch(requestURI::startsWith)) {
            return true;
        }

        // ✅ POST 요청에 대한 공용 URL
        if (method.equals("POST") && requestURI.equals("/api/v1/ai-images/result")) {
            return true;
        }

        return false;
    }

    /**
     * 사용자 인증 설정 (SecurityContext)
     */
    private void setAuthentication(String token, HttpServletRequest request) {
        Long userId = jwtService.extractUserId(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Access Token 헤더에서 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
    }

    /**
     * Refresh Token 쿠키에서 추출
     */
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

    /**
     * 클라이언트에서 Refresh Token 쿠키 삭제
     */
    private void clearRefreshToken(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie("refreshToken", null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(true);
        response.addCookie(expiredCookie);
    }
}
