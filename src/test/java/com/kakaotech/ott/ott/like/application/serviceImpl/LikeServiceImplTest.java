package com.kakaotech.ott.ott.like.application.serviceImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.like.domain.repository.LikeRepository;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.post.domain.repository.PostRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

    private final Long validUserId = 1L;
    private final Long validPostId = 1L;
    private final Long invalidUserId = 999L;
    private final Long invalidPostId = 999L;

    private LikeRequestDto likeRequestDto;

    @BeforeEach
    void setUp() {
        likeRequestDto = new LikeRequestDto(validPostId);
    }

    private void mockValidUserAndPost() {
        when(userAuthRepository.findById(validUserId)).thenReturn(mock(User.class));
        when(postRepository.findById(validPostId)).thenReturn(mock(Post.class));
    }


    @Test
    void 게시글에_좋아요_성공시_저장_및_카운트_증가_메서드_호출() {

        // given
        mockValidUserAndPost();
        when(likeRepository.existsByUserIdAndPostId(validUserId, validPostId)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(mock(Like.class));

        // when
        likeServiceImpl.likePost(validUserId, likeRequestDto);

        // then
        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(likeCaptor.capture());

        Like capturedLike = likeCaptor.getValue();

        assertNotNull(capturedLike);
        assertEquals(validUserId, capturedLike.getUserId());
        assertEquals(validPostId, capturedLike.getPostId());
        assertTrue(capturedLike.getIsActive());

    }

    @Test
    void 이미_좋아요한_게시글에_좋아요시_LIKE_ALREADY_EXISTS_예외발생() {

        // given
        mockValidUserAndPost();
        when(likeRepository.existsByUserIdAndPostId(validUserId, validPostId)).thenReturn(true);

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeServiceImpl.likePost(validUserId, likeRequestDto)
        );

        assertEquals(ErrorCode.LIKE_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void 게시글에_좋아요시_사용자_정보없음으로_USER_NOT_FOUND_예외발생() {

        // given
        when(userAuthRepository.findById(invalidUserId)).thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeServiceImpl.likePost(invalidUserId, likeRequestDto)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 게시글에_좋아요시_게시글_정보없음으로_POST_NOT_FOUND_예외발생() {

        // given
        when(userAuthRepository.findById(validUserId)).thenReturn(mock(User.class));
        when(postRepository.findById(invalidPostId)).thenThrow(new CustomException(ErrorCode.POST_NOT_FOUND));

        LikeRequestDto likeRequestDto = new LikeRequestDto(invalidPostId);

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeServiceImpl.likePost(validUserId, likeRequestDto)
        );

        verify(userAuthRepository, times(1)).findById(validUserId);
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }


    @Test
    void 좋아요한_게시글_취소시_삭제_및_카운트_감소_메서드_호출() {

        // given
        when(userAuthRepository.findById(validUserId)).thenReturn(mock(User.class));
        when(likeRepository.existsByUserIdAndPostId(validUserId, validPostId)).thenReturn(true);

        // when
        likeServiceImpl.unlikePost(validUserId, likeRequestDto);

        // then
        verify(userAuthRepository, times(1)).findById(validUserId);
        verify(likeRepository, times(1)).existsByUserIdAndPostId(validUserId, validPostId);
        verify(likeRepository, times(1)).deleteByUserEntityIdAndTargetId(validUserId, validPostId);
        verify(postRepository, times(1)).incrementLikeCount(validPostId, -1L);

    }

    @Test
    void 좋아요하지_않은_게시글_취소시_LIKE_NOT_FOUND_예외발생() {

        // given
        when(userAuthRepository.findById(validUserId)).thenReturn(mock(User.class));
        when(likeRepository.existsByUserIdAndPostId(validUserId, validPostId)).thenReturn(false);

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeServiceImpl.unlikePost(validUserId, likeRequestDto)
        );

        assertEquals(ErrorCode.LIKE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 게시글_좋아요취소시_사용자없음으로_USER_NOT_FOUND_예외발생() {

        // given
        when(userAuthRepository.findById(invalidUserId)).thenThrow(new CustomException(ErrorCode.USER_NOT_FOUND));

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeServiceImpl.unlikePost(invalidUserId, likeRequestDto)
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void 게시글_좋아요취소시_게시글없음으로_POST_NOT_FOUND_예외발생() {

        // given
        when(userAuthRepository.findById(validUserId)).thenReturn(mock(User.class));
        when(postRepository.findById(invalidPostId)).thenThrow(new CustomException(ErrorCode.POST_NOT_FOUND));

        LikeRequestDto likeRequestDto = new LikeRequestDto(invalidPostId);

        // when & then
        CustomException exception = assertThrows(
                CustomException.class,
                () -> likeServiceImpl.likePost(validUserId, likeRequestDto)
        );

        verify(userAuthRepository, times(1)).findById(validUserId);
        assertEquals(ErrorCode.POST_NOT_FOUND, exception.getErrorCode());
    }
    
}