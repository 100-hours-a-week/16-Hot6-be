package com.kakaotech.ott.ott.user.domain.repository;

import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;

import java.util.Optional;

public interface UserAuthRepository {

    Optional<User> findByEmail(String email);

    Optional<UserEntity> findById(Long userId);

    User save(User user);
}
