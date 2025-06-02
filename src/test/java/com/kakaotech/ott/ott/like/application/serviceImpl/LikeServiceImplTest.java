package com.kakaotech.ott.ott.like.application.serviceImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.model.LikeType;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    void 게시글에_좋아요_성공시_저장_및_카운트_증가_메서드_호출() {

        // given
        Long userId = 1L;
        LikeType type = LikeType.POST;
        Long targetId = 1L;

        LikeRequestDto likeRequestDto = new LikeRequestDto(type, targetId);

        when(userAuthRepository.findById(userId)).thenReturn(mock(User.class));
        when(postRepository.findById(likeRequestDto.getTargetId())).thenReturn(mock(Post.class));
        when(likeRepository.existsByUserIdAndPostId(userId, likeRequestDto.getTargetId())).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(mock(Like.class));

        // when
        likeServiceImpl.likePost(userId, likeRequestDto);

        // then
        verify(userAuthRepository, times(1)).findById(userId);
        verify(postRepository, times(1)).findById(likeRequestDto.getTargetId());
        verify(likeRepository, times(1)).existsByUserIdAndPostId(userId, likeRequestDto.getTargetId());
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(postRepository, times(1)).incrementLikeCount(likeRequestDto.getTargetId(), 1L);

    }

    @Test
    void 이미_좋아요한_게시글에_좋아요시_LIKE_ALREADY_EXISTS_예외발생() {

        // given
        Long userId = 1L;
        LikeType type = LikeType.POST;
        Long targetId = 1L;

        LikeRequestDto likeRequestDto = new LikeRequestDto(type, targetId);

        when(userAuthRepository.findById(userId)).thenReturn(mock(User.class));
        when(postRepository.findById(likeRequestDto.getTargetId())).thenReturn(mock(Post.class));
        when(likeRepository.existsByUserIdAndPostId(userId, likeRequestDto.getTargetId())).thenReturn(true);

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeServiceImpl.likePost(userId, likeRequestDto)
        );

        assertEquals(ErrorCode.LIKE_ALREADY_EXISTS, exception.getErrorCode());
        assertEquals("이미 좋아요한 게시글입니다.", exception.getMessage());
    }


    @Test
    void 좋아요한_게시글_취소시_삭제_및_카운트_감소_메서드_호출() {

        // given
        Long userId = 1L;
        LikeType type = LikeType.POST;
        Long targetId = 1L;

        LikeRequestDto likeRequestDto = new LikeRequestDto(type, targetId);

        when(userAuthRepository.findById(userId)).thenReturn(mock(User.class));
        when(likeRepository.existsByUserIdAndPostId(userId, likeRequestDto.getTargetId())).thenReturn(true);

        // when
        likeServiceImpl.unlikePost(userId, likeRequestDto);

        // then
        verify(userAuthRepository, times(1)).findById(userId);
        verify(likeRepository, times(1)).existsByUserIdAndPostId(userId, likeRequestDto.getTargetId());
        verify(likeRepository, times(1)).deleteByUserEntityIdAndTargetId(userId, likeRequestDto.getTargetId());
        verify(postRepository, times(1)).incrementLikeCount(likeRequestDto.getTargetId(), -1L);

    }

    @Test
    void 좋아요하지_않은_게시글_취소시_LIKE_NOT_FOUND_예외발생() {

        // given
        Long userId = 1L;
        LikeType type = LikeType.POST;
        Long targetId = 1L;

        LikeRequestDto likeRequestDto = new LikeRequestDto(type, targetId);

        when(userAuthRepository.findById(userId)).thenReturn(mock(User.class));
        when(likeRepository.existsByUserIdAndPostId(userId, likeRequestDto.getTargetId())).thenReturn(false);

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeServiceImpl.unlikePost(userId, likeRequestDto)
        );

        assertEquals(ErrorCode.LIKE_NOT_FOUND, exception.getErrorCode());
        assertEquals("좋아요하지 않은 게시글입니다.", exception.getMessage());
    }
}