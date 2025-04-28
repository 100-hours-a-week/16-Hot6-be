package com.kakaotech.ott.ott.user.repository;

import com.kakaotech.ott.ott.user.domain.User;
import com.kakaotech.ott.ott.user.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    User save(User user);

    Optional<UserEntity> findById(Long userId);
}
