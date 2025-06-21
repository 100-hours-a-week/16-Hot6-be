package com.kakaotech.ott.ott.user.domain.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private Role role;
    private boolean isActive;
    private Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String email, Role role, boolean isActive, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.isActive = isActive;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        return new UserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }


    public Long getId() {
        return id;
    }

    public boolean getIsActive() { return isActive; }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return ""; // 또는 null
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
