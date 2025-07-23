package com.kakaotech.ott.ott.like.infrastructure.sync;

import java.util.List;
import java.util.Map;

public interface LikeSyncService {
    void syncBatchWithVersion(List<Object[]> params, Map<Long, Long> deltas);
}
