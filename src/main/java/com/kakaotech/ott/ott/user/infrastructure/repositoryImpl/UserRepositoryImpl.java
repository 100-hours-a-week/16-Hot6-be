package com.kakaotech.ott.ott.user.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;


    @Override
    public User findById(Long userId) {

        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 사용자입니다."))
                .toDomain();
    }

    @Override
    @Transactional
    public User update(User user) {

        UserEntity userEntity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        if(!user.getImagePath().equals(userEntity.getImagePath()))
            userEntity.updateProfileImagePath(user.getImagePath());
        if(!user.getNicknameCommunity().equals(userEntity.getNicknameCommunity()))
            userEntity.updateNicknameCommunity(user.getNicknameCommunity());
        if(!user.getNicknameKakao().equals(userEntity.getNicknameKakao()))
            userEntity.updateNicknameKakao(user.getNicknameKakao());

        return userJpaRepository.save(userEntity).toDomain();
    }

    @Override
    @Transactional
    public void delete(User user) {

        UserEntity userEntity = userJpaRepository.findById(user.getId())
                        .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        userEntity.updateActive(user.isActive());
        userEntity.updateDeletedAt(user.getDeletedAt());

    }

    @Override
    @Transactional
    public void certify(User user) {

        UserEntity userEntity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        userEntity.updateVerified();
    }

    @Override
    public boolean existsByNicknameCommunity(String nicknameCommunity) {

        return userJpaRepository.existsByNicknameCommunity(nicknameCommunity);
    }


}
