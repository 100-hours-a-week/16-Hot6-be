package com.kakaotech.ott.ott.like.domain.repository;

import com.kakaotech.ott.ott.like.domain.model.Like;

import java.util.List;
import java.util.Set;

public interface LikeRepository {

    void deleteByUserEntityIdAndTargetId(Long userId, Long postId);

    Like save(Like like);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    int findByPostId(Long postId);

    Set<Long> findLikedPostIdsByUserId(Long userId, List<Long> postIds);
}
