package com.kakaotech.ott.ott.global.security;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.user.application.serviceImpl.JwtService;
import com.kakaotech.ott.ott.user.application.serviceImpl.CustomUserDetailsService;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
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

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveToken(request);
        String refreshToken = getRefreshTokenFromCookie(request);

        try {
            if (accessToken != null && jwtService.validateToken(accessToken)) {
                setAuthentication(accessToken, request);
            } else if (refreshToken != null) {
                Long userId = jwtService.extractUserId(refreshToken);

                if (jwtService.validateToken(refreshToken, userId)) {
                    String newAccessToken = jwtService.reissueAccessToken(refreshToken);
                    setAuthentication(newAccessToken, request);
                }
            }
        } catch (ExpiredJwtException ex) {
            clearRefreshToken(response);
            response.sendRedirect("/oauth2/authorization/kakao");
            return;
        } catch (JwtException ex) {
            clearRefreshToken(response);
            response.sendRedirect("/oauth2/authorization/kakao");
            return;
        }

        // ✅ 인증 완료 후, 탈퇴된 사용자 예외 확인
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!userPrincipal.getIsActive() && !isRecoveryRequest(request)) {
                throw new CustomException(ErrorCode.USER_DELETED);
            }
        }


        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token, HttpServletRequest request) {
        Long userId = jwtService.extractUserId(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId.toString());

//        // ✅ 사용자 탈퇴 상태 확인
//        if (userDetails instanceof UserPrincipal) {
//            UserPrincipal userPrincipal = (UserPrincipal) userDetails;
//            if (!userPrincipal.getIsActive()) {
//                throw new CustomException(ErrorCode.USER_DELETED);
//            }
//        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // ✅ 복구 API 경로 확인
    private boolean isRecoveryRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/api/v1/users/recover");
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer ")) ? bearer.substring(7) : null;
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

    private void clearRefreshToken(HttpServletResponse response) {
        Cookie expiredCookie = new Cookie("refreshToken", null);
        expiredCookie.setMaxAge(0);
        expiredCookie.setPath("/");
        expiredCookie.setHttpOnly(true);
        expiredCookie.setSecure(true);
        response.addCookie(expiredCookie);
    }
}
