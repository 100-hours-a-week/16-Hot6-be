package com.kakaotech.ott.ott.pointHistory.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryJpaRepository;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryRepository;
import com.kakaotech.ott.ott.pointHistory.infrastructure.entity.PointHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Slice<PointHistory> findUserPointHistory(Long userId, Long cursorId, int size) {

        Slice<PointHistoryEntity> slice = pointHistoryJpaRepository.findUserAllPointHistory(userId, cursorId, PageRequest.of(0, size));

        return slice.map(PointHistoryEntity::toDomain);
    }
}
