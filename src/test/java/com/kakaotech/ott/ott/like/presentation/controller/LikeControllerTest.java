package com.kakaotech.ott.ott.like.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.like.application.service.LikeService;
import com.kakaotech.ott.ott.like.domain.model.LikeType;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
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

    @Test
    void POST_요청으로_좋아요가_성공적으로_요청된다() {

        // given
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getId()).thenReturn(1L);

        Long userId = userPrincipal.getId();
        LikeRequestDto likeRequestDto = new LikeRequestDto(LikeType.POST, 1L);

        // when
        ResponseEntity<ApiResponse> result = likeController.activeLike(userPrincipal, likeRequestDto);

        // then
        verify(likeService, times(1)).likePost(userId, likeRequestDto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals("좋아요 완료", result.getBody().getMessage());
        assertNull(result.getBody().getData());
    }

    @Test
    void DELETE_요청으로_좋아요가_비활성화된다() {

        // given
        UserPrincipal userPrincipal = mock(UserPrincipal.class);
        when(userPrincipal.getId()).thenReturn(1L);

        Long userId = userPrincipal.getId();
        LikeRequestDto likeRequestDto = new LikeRequestDto(LikeType.POST, 1L);

        // when
        ResponseEntity<Void> result = likeController.deactiveLike(userPrincipal, likeRequestDto);

        // then
        verify(likeService, times(1)).unlikePost(userId, likeRequestDto);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }
}