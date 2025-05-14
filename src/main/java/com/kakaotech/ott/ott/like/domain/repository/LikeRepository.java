package com.kakaotech.ott.ott.like.domain.repository;

import com.kakaotech.ott.ott.like.domain.model.Like;

public interface LikeRepository {

    void deleteByUserEntityIdAndTypeAndTargetId(Long userId, Long postId);

    Like save(Like like);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    int findByPostId(Long postId);
}
