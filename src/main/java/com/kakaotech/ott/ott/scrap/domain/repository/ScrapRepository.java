package com.kakaotech.ott.ott.scrap.domain.repository;

import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.scrap.domain.model.Scrap;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import org.springframework.data.domain.Slice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface ScrapRepository {

    void deleteByUserEntityIdAndTypeAndTargetId(Long userId, Long postId);

    Scrap save(Scrap scrap);

    boolean existsByUserIdAndTypeAndPostId(Long userId, ScrapType scrapType, Long postId);

    Set<Long> findScrappedPostIds(Long userId, List<Long> postIds);

    Set<Long> findScrappedServiceProductIds(Long userId, List<Long> productIds);

    int findByPostId(Long postId, ScrapType type);

    Slice<Scrap> findUserScrap(Long userId, Long cursorId, int size);
}
