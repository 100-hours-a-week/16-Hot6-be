package com.kakaotech.ott.ott.pointHistory.domain.repository;

import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.user.domain.model.User;
import org.springframework.data.domain.Slice;

public interface PointHistoryRepository {

    Slice<PointHistory> findUserPointHistory(Long userId, Long cursorId, int size);

    PointHistory save(PointHistory pointHistory, User user);

    PointHistory findLatestPointHistoryByUserId(Long userId);
}
