package com.kakaotech.ott.ott.user.infrastructure.repository;

import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {

        return userJpaRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.from(user); // Domain → Entity 변환
        return userJpaRepository.save(entity).toDomain(); // 저장 후 Domain 반환
    }
}