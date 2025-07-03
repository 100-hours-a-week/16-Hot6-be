package com.kakaotech.ott.ott.scrap.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.scrap.domain.repository.ScrapQueryRepository;
import com.kakaotech.ott.ott.scrap.infrastructure.entity.QScrapEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ScrapQueryRepositoryImpl implements ScrapQueryRepository {

    private final JPAQueryFactory queryFactory;

    QScrapEntity scrap = QScrapEntity.scrapEntity;

    @Override
    public Map<Long, Boolean> findScrapMapByUserIdAndVariantIds(Long userId, List<Long> variantIds) {
        if (userId == null || variantIds == null || variantIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return queryFactory
                .select(scrap.targetId, scrap.id.count().gt(0))
                .from(scrap)
                .where(scrap.userEntity.id.eq(userId), scrap.targetId.in(variantIds))
                .groupBy(scrap.targetId)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(scrap.targetId),
                        tuple -> tuple.get(scrap.id.count().gt(0))
                ));
    }

}
