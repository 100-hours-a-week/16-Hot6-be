package com.kakaotech.ott.ott.like.application.serviceImpl;

import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.model.LikeType;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeServiceImpl likeServiceImpl;

    @Test
    @DisplayName("게시글에 대한 좋아요 생성 로직이 정상적으로 동작된다.")
    void likePost() {

        // given
        Long userId = 1L;
        LikeType type = LikeType.POST;
        Long targetId = 1L;

        LikeRequestDto likeRequestDto = new LikeRequestDto(type, targetId);

        User user = mock(User.class);
        Post post = mock(Post.class);

        when(userAuthRepository.findById(userId)).thenReturn(user);
        when(postRepository.findById(likeRequestDto.getTargetId())).thenReturn(post);
        when(likeRepository.existsByUserIdAndPostId(userId, likeRequestDto.getTargetId())).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(mock(Like.class));

        // when
        likeServiceImpl.likePost(userId, likeRequestDto);

        // then
        verify(userAuthRepository).findById(userId);
        verify(postRepository).findById(likeRequestDto.getTargetId());
        verify(likeRepository).existsByUserIdAndPostId(userId, likeRequestDto.getTargetId());
        verify(likeRepository).save(any(Like.class));
        verify(postRepository).incrementLikeCount(likeRequestDto.getTargetId(), 1L);

    }

    @Test
    void unlikePost() {

        // given
        Long userId = 1L;
        LikeType type = LikeType.POST;
        Long targetId = 1L;

        LikeRequestDto likeRequestDto = new LikeRequestDto(type, targetId);

        User user = mock(User.class);
        Post post = mock(Post.class);

        when(userAuthRepository.findById(userId)).thenReturn(user);
        when(likeRepository.existsByUserIdAndPostId(userId, likeRequestDto.getTargetId())).thenReturn(true);

        // when
        likeServiceImpl.unlikePost(userId, likeRequestDto);

        // then
        verify(userAuthRepository).findById(userId);
        verify(likeRepository).existsByUserIdAndPostId(userId, likeRequestDto.getTargetId());
        verify(likeRepository).deleteByUserEntityIdAndTargetId(userId, likeRequestDto.getTargetId());
        verify(postRepository).incrementLikeCount(likeRequestDto.getTargetId(), -1L);

    }
}