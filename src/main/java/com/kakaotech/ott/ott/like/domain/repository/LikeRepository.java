package com.kakaotech.ott.ott.like.domain.repository;

import java.util.List;
import java.util.Set;

public interface LikeRepository {

    void deleteByUserEntityIdAndTargetId(Long userId, Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    Long findByPostId(Long postId);

    Set<Long> findLikedPostIdsByUserId(Long userId, List<Long> postIds);
}
