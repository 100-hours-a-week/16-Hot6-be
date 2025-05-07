package com.kakaotech.ott.ott.like.application.service;

import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;

public interface LikeService {

    void likePost(Long userId, LikeRequestDto likeRequestDto);

    void unlikePost(Long userId, LikeRequestDto likeRequestDto);
}
