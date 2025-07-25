package com.kakaotech.ott.ott.user.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserAuthRepositoryImpl implements UserAuthRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByEmail(String email) {

        return userJpaRepository.findByEmail(email).map(UserEntity::toDomain);
    }

    @Override
    public User findById(Long userId) {

        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .toDomain();
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.from(user); // Domain → Entity 변환
        return userJpaRepository.save(entity).toDomain(); // 저장 후 Domain 반환
    }

    @Override
    public List<User> findAllById(Collection<Long> userIds) {

        return userJpaRepository.findAllById(userIds).stream()
                .map(UserEntity::toDomain)
                .collect(Collectors.toList());
    }
}