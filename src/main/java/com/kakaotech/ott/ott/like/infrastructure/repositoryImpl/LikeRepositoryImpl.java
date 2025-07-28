package com.kakaotech.ott.ott.like.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.like.domain.repository.LikeJpaRepository;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class LikeRepositoryImpl implements LikeRepository {

    private final LikeJpaRepository likeJpaRepository;

    @Override
    public void deleteByUserEntityIdAndTargetId(Long userId, Long postId) {
        likeJpaRepository.deleteByUserEntityIdAndTargetId(userId, postId);
    }

    @Override
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return likeJpaRepository.existsActiveByUserIdAndPostId(userId, postId);
    }

    @Override
    public Long findByPostId(Long postId) {
        return likeJpaRepository.countByPostId(postId);
    }

    @Override
    public Set<Long> findLikedPostIdsByUserId(Long userId, List<Long> postIds) {
        return new HashSet<>(likeJpaRepository.findLikedPostIds(userId, postIds));
    }


}
