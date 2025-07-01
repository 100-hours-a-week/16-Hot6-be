package com.kakaotech.ott.ott.post.domain.repository;

import com.kakaotech.ott.ott.post.presentation.dto.response.PostAllResponseDto;


public interface PostQueryRepository {

    PostAllResponseDto getAllPost(Long userId, String category, String sort, int size,
                                  Long lastPostId, Integer lastLikeCount, Long lastViewCount, Double lastWeightCount);
}
