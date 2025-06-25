package com.kakaotech.ott.ott.pointHistory.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryJpaRepository;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryRepository;
import com.kakaotech.ott.ott.pointHistory.infrastructure.entity.PointHistoryEntity;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepositoryImpl implements PointHistoryRepository {

    private final PointHistoryJpaRepository pointHistoryJpaRepository;

    @Override
    public Slice<PointHistory> findUserPointHistory(Long userId, Long cursorId, int size) {

        Slice<PointHistoryEntity> slice = pointHistoryJpaRepository.findUserAllPointHistory(userId, cursorId, PageRequest.of(0, size));

        return slice.map(PointHistoryEntity::toDomain);
    }

    @Override
    public PointHistory save(PointHistory pointHistory, User user) {

        PointHistoryEntity pointHistoryEntity = PointHistoryEntity.from(pointHistory, UserEntity.from(user));

        return pointHistoryJpaRepository.save(pointHistoryEntity).toDomain();
    }

    @Override
    public PointHistory findLatestPointHistoryByUserId(Long userId) {

        PointHistoryEntity pointHistoryEntity = pointHistoryJpaRepository.findFirstByUserEntityIdOrderByIdDesc(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.POINT_HISTORY_NOT_FOUNR));

        return pointHistoryEntity.toDomain();
    }

}
