package com.kakaotech.ott.ott.scrap.domain.repository;

import com.kakaotech.ott.ott.scrap.domain.model.Scrap;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;

import java.util.List;
import java.util.Set;

public interface ScrapRepository {

    void deleteByUserEntityIdAndTypeAndTargetId(Long userId, Long postId);

    Scrap save(Scrap scrap);

    boolean existsByUserIdAndTypeAndPostId(Long userId, ScrapType scrapType, Long postId);

    Set<Long> findScrappedPostIds(Long userId, List<Long> postIds);

    int findByPostId(Long postId);
}
