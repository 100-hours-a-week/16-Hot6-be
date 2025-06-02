package com.kakaotech.ott.ott.like.config;

import com.kakaotech.ott.ott.user.domain.model.Role;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        // 유저 객체 생성 (여기서 UserPrincipal은 유찬님의 프로젝트에 맞게 생성)
        UserPrincipal principal = new UserPrincipal(annotation.id(), annotation.email(), Role.USER, true, List.of(new SimpleGrantedAuthority("ROLE_" + annotation.roles())));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                "password",
                principal.getAuthorities()
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}

