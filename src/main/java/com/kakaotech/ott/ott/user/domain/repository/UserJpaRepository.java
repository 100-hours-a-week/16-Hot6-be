package com.kakaotech.ott.ott.user.domain.repository;

import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByNicknameCommunity(String nicknameCommunity);
}
