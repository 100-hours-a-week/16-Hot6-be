package com.kakaotech.ott.ott.user.domain.repository;

import com.kakaotech.ott.ott.user.domain.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserAuthRepository {

    Optional<User> findByEmail(String email);

    User findById(Long userId);

    User save(User user);

    List<User> findAllById(Collection<Long> userIds);
}
