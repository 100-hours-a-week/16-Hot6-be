package com.kakaotech.ott.ott.post.presentation.controller;

import com.kakaotech.ott.ott.global.response.ApiResponse;
import com.kakaotech.ott.ott.post.application.component.ViewCountAggregator;
import com.kakaotech.ott.ott.post.application.service.PostService;
import com.kakaotech.ott.ott.post.presentation.dto.request.AiPostCreateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.request.AiPostUpdateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.request.FreePostCreateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.request.FreePostUpdateRequestDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostCreateResponseDto;
import com.kakaotech.ott.ott.post.presentation.dto.response.PostGetResponseDto;
import com.kakaotech.ott.ott.user.domain.model.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    private final ViewCountAggregator viewCountAggregator;

    @GetMapping
    public ResponseEntity<ApiResponse<PostAllResponseDto>> getAllPostS(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long lastPostId
    ) {
        PostAllResponseDto payload = postService.getAllPost(userPrincipal.getId(), size, lastPostId);
        return ResponseEntity.ok(ApiResponse.success("게시글 목록 조회 성공", payload));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostGetResponseDto>> getPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId) {

        Long userId = userPrincipal.getId();

        return ResponseEntity.ok(ApiResponse.success("게시글 조회 성공", postService.getPost(postId, userId)));
    }

    @PostMapping("/free")
    public ResponseEntity<ApiResponse<PostCreateResponseDto>> createFreePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @ModelAttribute FreePostCreateRequestDto freePostCreateRequestDto) throws IOException {

        Long userId = userPrincipal.getId();
        PostCreateResponseDto postCreateResponseDto = postService.createFreePost(freePostCreateRequestDto, userId);

        return ResponseEntity.ok(ApiResponse.success("자유 게시판 게시글 작성 완료", postCreateResponseDto));
    }


    @PostMapping("/ai")
    public ResponseEntity<ApiResponse<PostCreateResponseDto>> createAiPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestBody @Valid AiPostCreateRequestDto aiPostCreateRequestDto) {

        Long userId = userPrincipal.getId();
        PostCreateResponseDto postCreateResponseDto = postService.createAiPost(aiPostCreateRequestDto, userId);

        return ResponseEntity.ok(ApiResponse.success("AI 게시판 게시글 작성 완료", postCreateResponseDto));
    }

    @PatchMapping("/free/{postId}")
    public ResponseEntity<ApiResponse<PostCreateResponseDto>> updateFreePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @Valid @ModelAttribute FreePostUpdateRequestDto freePostUpdateRequestDto) throws IOException {

        Long userId = userPrincipal.getId();
        PostCreateResponseDto postCreateResponseDto = postService.updateFreePost(postId, userId, freePostUpdateRequestDto);

        return ResponseEntity.ok(ApiResponse.success("게시글 수정 완료", postCreateResponseDto));
    }

    @PatchMapping(value = "/ai/{postId}")
    public ResponseEntity<ApiResponse<PostCreateResponseDto>> updateAiPost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId,
            @Valid @RequestBody AiPostUpdateRequestDto aiPostUpdateRequestDto) throws IOException {

        Long userId = userPrincipal.getId();
        PostCreateResponseDto postCreateResponseDto = postService.updateAiPost(postId, userId, aiPostUpdateRequestDto);

        return ResponseEntity.ok(ApiResponse.success("게시글 수정 완료", postCreateResponseDto));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> deletePost(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long postId) throws AccessDeniedException {

        Long userId = userPrincipal.getId();

        postService.deletePost(userId, postId);

        return ResponseEntity.ok(ApiResponse.success("게시글 삭제 완료", null));
    }


}
