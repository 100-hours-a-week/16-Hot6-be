package com.kakaotech.ott.ott.scrap.domain.repository;

import com.kakaotech.ott.ott.scrap.domain.model.Scrap;

public interface ScrapRepository {

    void deleteByUserEntityIdAndTypeAndTargetId(Long userId, Long postId);

    Scrap save(Scrap scrap);

    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
