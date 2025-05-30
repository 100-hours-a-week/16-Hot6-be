package com.kakaotech.ott.ott.like.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.repository.LikeJpaRepository;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.like.infrastructure.entity.LikeEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public void deleteByUserEntityIdAndTargetId(Long userId, Long postId) {
        likeJpaRepository.deleteByUserEntityIdAndTargetId(userId, postId);
    }

    @Override
    @Transactional
    public Like save(Like like) {

        UserEntity userEntity = userJpaRepository.findById(like.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        LikeEntity likeEntity = LikeEntity.from(like, userEntity);
        LikeEntity savedLikeEntity = likeJpaRepository.save(likeEntity);

        return savedLikeEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return likeJpaRepository.existsByUserEntityIdAndTargetId(userId, postId);
    }

    @Override
    public int findByPostId(Long postId) {
        return likeJpaRepository.countByPostId(postId);
    }

    @Override
    public Set<Long> findLikedPostIdsByUserId(Long userId, List<Long> postIds) {
        return new HashSet<>(likeJpaRepository.findLikedPostIds(userId, postIds));
    }


}
