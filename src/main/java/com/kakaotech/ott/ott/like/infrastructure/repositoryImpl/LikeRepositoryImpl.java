package com.kakaotech.ott.ott.like.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.repository.LikeJpaRepository;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.like.infrastructure.entity.LikeEntity;
import com.kakaotech.ott.ott.post.domain.repository.PostJpaRepository;
import com.kakaotech.ott.ott.post.infrastructure.entity.PostEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
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
    private final PostJpaRepository postJpaRepository;

    @Override
    @Transactional
    public void deleteByUserEntityIdAndTargetId(Long userId, Long postId) {
        likeJpaRepository.deleteByUserEntityIdAndTargetId(userId, postId);
    }

    @Override
    @Transactional
    public Like save(Like like) {

        UserEntity userEntity = userJpaRepository.findById(like.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        PostEntity postEntity = postJpaRepository.findById(like.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        return likeJpaRepository.save(LikeEntity.from(like, userEntity, postEntity)).toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return likeJpaRepository.existsByUserEntityIdAndPostEntityId(userId, postId);
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
