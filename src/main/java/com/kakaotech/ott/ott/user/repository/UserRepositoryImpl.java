package com.kakaotech.ott.ott.user.repository;

import com.kakaotech.ott.ott.user.domain.User;
import com.kakaotech.ott.ott.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository{

    private final UserJpaRepository userJpaRepository;
    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(UserEntity::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userJpaRepository.save(user.toEntity());
        return userEntity.toDomain();
    }

    @Override
    public Optional<UserEntity> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }
}
