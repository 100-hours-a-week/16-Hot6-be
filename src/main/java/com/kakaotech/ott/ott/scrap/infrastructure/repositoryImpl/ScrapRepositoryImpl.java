package com.kakaotech.ott.ott.scrap.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.like.domain.model.Like;
import com.kakaotech.ott.ott.scrap.domain.model.Scrap;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapJpaRepository;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import com.kakaotech.ott.ott.scrap.infrastructure.entity.ScrapEntity;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import com.kakaotech.ott.ott.user.domain.repository.UserJpaRepository;
import jakarta.persistence.EntityManager;
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
    private final EntityManager em;

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
        return scrapJpaRepository.existsActiveByUserEntityIdAndTypeAndTargetId(userId, scrapType, postId);
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

    @Override
    public void bulkInsertOrReactivateWithBatch(List<Scrap> scraps, int batchSize) {
        if (scraps == null || scraps.isEmpty()) return;
        if (batchSize <= 0) batchSize = 500;

        for (int i = 0; i < scraps.size(); i += batchSize) {
            int end = Math.min(i + batchSize, scraps.size());
            List<Scrap> subList = scraps.subList(i, end);
            bulkInsertOrReactivate(subList);
        }
    }

    private void bulkInsertOrReactivate(List<Scrap> scraps) {
        if (scraps.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("""
        INSERT INTO scraps (user_id, type, target_id, is_active, created_at)
        VALUES
    """);

        for (int i = 0; i < scraps.size(); i++) {
            Scrap scrap = scraps.get(i);
            sb.append("(")
                    .append(scrap.getUserId()).append(", ")
                    .append("'").append(scrap.getType()).append("', ")
                    .append(scrap.getTargetId()).append(", ")
                    .append(true).append(", ")
                    .append("now()")
                    .append(")");
            if (i < scraps.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("""
        ON DUPLICATE KEY UPDATE
        is_active = true
    """);

        em.createNativeQuery(sb.toString()).executeUpdate();
    }


    @Override
    public void bulkDeactivateWithBatch(List<Scrap> scraps, int batchSize) {
        if (scraps == null || scraps.isEmpty()) return;
        if (batchSize <= 0) batchSize = 500;

        for (int i = 0; i < scraps.size(); i += batchSize) {
            int end = Math.min(i + batchSize, scraps.size());
            List<Scrap> subList = scraps.subList(i, end);
            bulkDeactivate(subList);
        }
    }

    private void bulkDeactivate(List<Scrap> scraps) {
        if (scraps.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        sb.append("""
        UPDATE scraps
        SET is_active = false
        WHERE (user_id, type, target_id) IN (
    """);

        for (int i = 0; i < scraps.size(); i++) {
            Scrap scrap = scraps.get(i);
            sb.append("(")
                    .append(scrap.getUserId()).append(", ")
                    .append("'").append(scrap.getType()).append("', ")
                    .append(scrap.getTargetId()).append(")");
            if (i < scraps.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append(")");

        em.createNativeQuery(sb.toString()).executeUpdate();
    }

}
