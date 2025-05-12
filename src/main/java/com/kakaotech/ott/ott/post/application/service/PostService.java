package com.kakaotech.ott.ott.post.application.service;

import com.kakaotech.ott.ott.post.presentation.dto.request.AiPostCreateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.request.AiPostUpdateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.request.FreePostUpdateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.PopularSetupDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;
import com.kakaotech.ott.ott.post.presentation.dto.request.FreePostCreateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostCreateResponseDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostGetResponseDto;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

public interface PostService {

    PostCreateResponseDto createFreePost(FreePostCreateRequestDto freePostCreateRequestDto, Long userId) throws IOException;

    PostCreateResponseDto createAiPost(AiPostCreateRequestDto aiPostCreateRequestDto, Long userId);

    PostAllResponseDto getAllPost(Long userId, LocalDateTime lastCreatedAt, String category, String sort, int size, Long lastPostId);

    PostGetResponseDto getPost(Long postId, Long userId);

    void deletePost(Long userId, Long postId) throws AccessDeniedException;

    PostCreateResponseDto updateFreePost(Long postId, Long userId, FreePostUpdateRequestDto freePostUpdateRequestDto) throws IOException;

    PostCreateResponseDto updateAiPost(Long postId, Long userId, AiPostUpdateRequestDto aiPostUpdateRequestDto) throws IOException;

    List<PopularSetupDto> getPopularSetups(Long userId);
}
