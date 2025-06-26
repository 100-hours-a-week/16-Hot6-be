package com.kakaotech.ott.ott.scrap.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.scrap.domain.model.Scrap;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapJpaRepository;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import com.kakaotech.ott.ott.scrap.infrastructure.entity.ScrapEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ScrapRepositoryImpl implements ScrapRepository {

    private final ScrapJpaRepository scrapJpaRepository;
    private final UserJpaRepository userJpaRepository;

    @Override
    @Transactional
    public void deleteByUserEntityIdAndTypeAndTargetId(Long userId, Long postId) {
        scrapJpaRepository.deleteByUserEntityIdAndTargetId(userId, postId);
    }

    @Override
    @Transactional
    public Scrap save(Scrap scrap) {

        UserEntity userEntity = userJpaRepository.findById(scrap.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자가 존재하지 않습니다."));

        ScrapEntity scrapEntity = ScrapEntity.from(scrap, userEntity);
        ScrapEntity savedscrapEntity = scrapJpaRepository.save(scrapEntity);

        return savedscrapEntity.toDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserIdAndTypeAndPostId(Long userId, ScrapType scrapType, Long postId) {
        return scrapJpaRepository.existsByUserEntityIdAndTypeAndTargetId(userId, scrapType, postId);
    }

    @Override
    public Set<Long> findScrappedPostIds(Long userId, List<Long> postIds) {
        return new HashSet<>(scrapJpaRepository.findScrappedPostIds(userId, postIds));
    }

    @Override
    public Set<Long> findScrappedServiceProductIds(Long userId, List<Long> productIds) {
        return new HashSet<>(scrapJpaRepository.findScrappedServiceProductIds(userId, productIds));
    }

    @Override
    public int findByPostId(Long postId, ScrapType type) {
        return scrapJpaRepository.countByPostId(postId);
    }

    @Override
    public Slice<Scrap> findUserScrap(Long userId, Long cursorId, int size) {

        Slice<ScrapEntity> slice = scrapJpaRepository.findUserAllScraps(userId, cursorId, PageRequest.of(0, size));

        return slice.map(ScrapEntity::toDomain);
    }

}
