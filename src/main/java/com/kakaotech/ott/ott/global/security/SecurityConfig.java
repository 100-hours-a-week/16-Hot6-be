package com.kakaotech.ott.ott.global.security;

import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.user.application.serviceImpl.JwtService;
import com.kakaotech.ott.ott.user.presentation.controller.OAuth2FailureHandler;
import com.kakaotech.ott.ott.user.application.serviceImpl.CustomOAuth2UserService;
import com.kakaotech.ott.ott.user.presentation.controller.OAuth2SuccessHandler;
import com.kakaotech.ott.ott.user.application.serviceImpl.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final OAuth2FailureHandler failureHandler;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // ✅ CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ CORS 설정 적용
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ✅ 세션 사용 비활성화 (무상태)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, publicUrls().toArray(new String[0])).permitAll()
                        // ✅ POST 요청 허용
                        .requestMatchers(HttpMethod.POST,  "/api/v1/ai-images/result", "/api/v1/auth/kakao").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, ex1) -> {
                            if (isApiRequest(req)) {
                                res.setStatus(ErrorCode.AUTH_REQUIRED.getHttpStatus().value());
                                res.setContentType("application/json;charset=UTF-8");

                                String jsonResponse = String.format(
                                        "{\"status\":%d,\"message\":\"%s\",\"code\":\"%s\"}",
                                        ErrorCode.AUTH_REQUIRED.getHttpStatus().value(),
                                        ErrorCode.AUTH_REQUIRED.getMessage(),
                                        ErrorCode.AUTH_REQUIRED.name()
                                );

                                res.getWriter().write(jsonResponse);
                            } else {
                                res.sendError(ErrorCode.AUTH_REQUIRED.getHttpStatus().value(), ErrorCode.AUTH_REQUIRED.getMessage());
                            }
                        })
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(endpoint -> endpoint.baseUri("/oauth2/authorization"))
                        .userInfoEndpoint(info -> info.userService(customOAuth2UserService))
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ API 요청인지 확인하는 메서드
    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }

    // ✅ CORS 정책을 SecurityConfig에서 직접 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("https://dev.onthe-top.com");
        config.addAllowedOriginPattern("https://backend.onthe-top.com");
        config.addAllowedOriginPattern("https://onthe-top.com");
        config.addAllowedOriginPattern("http://localhost:3000");
        config.addAllowedOriginPattern("http://10.50.0.3:8000");
        config.addAllowedMethod("*"); // GET, POST, PUT, DELETE 등 모든 HTTP 메서드 허용
        config.addAllowedHeader("*"); // 모든 헤더 허용
        config.setExposedHeaders(List.of("Authorization", "Set-Cookie")); // 쿠키 및 Authorization 헤더 노출

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public List<String> publicUrls() {
        return List.of(
                "/api/v1/main",
                "/api/v1/health",
                "/api/v1/posts",
                "/api/v1/posts/{postId}",
                "/api/v1/posts/{postId}/comments",
                "/api/v1/desk-products",
                "/api/v1/auth/kakao",
                "/oauth2/authorization/kakao",
                "/login/**"
        );
    }
}
