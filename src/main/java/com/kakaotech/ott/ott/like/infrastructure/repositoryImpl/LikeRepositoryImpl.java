package com.kakaotech.ott.ott.like.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.repository.LikeJpaRepository;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.like.infrastructure.entity.LikeEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.user.infrastructure.repository.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    public void deleteByUserEntityIdAndTypeAndTargetId(Long userId, Long postId) {
        likeJpaRepository.deleteByUserEntityIdAndTargetId(userId, postId);
    }

    @Override
    public Like save(Like like) {

        UserEntity userEntity = userJpaRepository.findById(like.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        LikeEntity likeEntity = LikeEntity.from(like, userEntity);
        LikeEntity savedLikeEntity = likeJpaRepository.save(likeEntity);

        return savedLikeEntity.toDomain();
    }

    @Override
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return likeJpaRepository.existsByUserEntityIdAndTargetId(userId, postId);
    }


}
