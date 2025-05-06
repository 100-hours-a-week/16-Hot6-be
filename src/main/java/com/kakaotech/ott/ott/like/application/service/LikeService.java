package com.kakaotech.ott.ott.like.application.service;

import com.kakaotech.ott.ott.like.presentation.dto.request.LikeActiveRequestDto;

public interface LikeService {

    void likePost(Long userId, LikeActiveRequestDto likeActiveRequestDto);
}
