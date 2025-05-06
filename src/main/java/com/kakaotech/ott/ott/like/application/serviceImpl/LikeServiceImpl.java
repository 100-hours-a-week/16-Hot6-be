package com.kakaotech.ott.ott.like.application.serviceImpl;

import com.kakaotech.ott.ott.like.application.service.LikeService;
import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.model.LikeType;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeActiveRequestDto;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    @Override
    public void likePost(Long userId, LikeActiveRequestDto likeActiveRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."))
                .toDomain();
        Post post = postRepository.findById(likeActiveRequestDto.getTargetId());

        boolean exists = likeRepository.existsByUserIdAndPostId(userId, likeActiveRequestDto.getTargetId());

        // 이미 좋아요 상태라면 아무 동작 하지 않음
        if(exists)
            return;

        Like like = Like.createLike(userId, LikeType.POST, likeActiveRequestDto.getTargetId());
        Like savedLike = likeRepository.save(like);

        postRepository.incrementLikeCount(likeActiveRequestDto.getTargetId(), 1L);
    }
}
