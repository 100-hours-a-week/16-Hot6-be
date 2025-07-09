package com.kakaotech.ott.ott.like.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.like.application.service.LikeService;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private Long userId;
    private final Long postId = 1L;
    private LikeRequestDto likeRequestDto;
    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getId()).thenReturn(1L);

        userId = userPrincipal.getId();

        likeRequestDto = new LikeRequestDto(postId);
    }

    @Test
    void 로그인_사용자가_게시글_좋아요_요청하면_201_반환() {

        // when
        ResponseEntity<ApiResponse> result = likeController.activeLike(userPrincipal, likeRequestDto);

        // then
        verify(likeService, times(1)).likePost(userId, likeRequestDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("좋아요 완료", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    @Test
    void 로그인_사용자가_게시글_좋아요_취소_요청하면_204_반환() {

        // when
        ResponseEntity<Void> result = likeController.deactiveLike(userPrincipal, likeRequestDto);

        // then
        verify(likeService, times(1)).unlikePost(userId, likeRequestDto);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}
