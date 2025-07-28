package com.kakaotech.ott.ott.scrap.infrastructure.sycn;

import java.util.List;
import java.util.Map;

public interface ScrapSyncService {
    void syncBatchWithVersion(List<Object[]> params, Map<String, Long> deltas);
}
