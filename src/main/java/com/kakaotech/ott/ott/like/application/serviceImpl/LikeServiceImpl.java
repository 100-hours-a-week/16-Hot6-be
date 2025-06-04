package com.kakaotech.ott.ott.like.application.serviceImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.like.application.service.LikeService;
import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.model.LikeType;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserAuthRepository userAuthRepository;
    private final PostRepository postRepository;

    @Transactional
    @Override
    public void likePost(Long userId, LikeRequestDto likeRequestDto) {

        User user = userAuthRepository.findById(userId);

        Post post = postRepository.findById(likeRequestDto.getTargetId());

        boolean exists = likeRepository.existsByUserIdAndPostId(userId, likeRequestDto.getTargetId());

        // 이미 좋아요 상태라면 아무 동작 하지 않음
        if(exists) {
            throw new CustomException(ErrorCode.LIKE_ALREADY_EXISTS);
        }

        Like like = Like.createLike(userId, LikeType.POST, likeRequestDto.getTargetId());
        Like savedLike = likeRepository.save(like);

        postRepository.incrementLikeCount(likeRequestDto.getTargetId(), 1L);
    }

    @Transactional
    @Override
    public void unlikePost(Long userId, LikeRequestDto likeRequestDto) {

        User user = userAuthRepository.findById(userId);

        Post post = postRepository.findById(likeRequestDto.getTargetId());

        boolean exists = likeRepository.existsByUserIdAndPostId(userId, likeRequestDto.getTargetId());

        // 이미 좋아요 상태라면 아무 동작 하지 않음
        if(!exists) {
            throw new CustomException(ErrorCode.LIKE_NOT_FOUND);
        }

        likeRepository.deleteByUserEntityIdAndTargetId(userId, likeRequestDto.getTargetId());

        postRepository.incrementLikeCount(likeRequestDto.getTargetId(), -1L);
    }
}
