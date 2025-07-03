package com.kakaotech.ott.ott.scrap.domain.repository;

import java.util.List;
import java.util.Map;

public interface ScrapQueryRepository {

    Map<Long, Boolean> findScrapMapByUserIdAndVariantIds(Long userId, List<Long> variantIds);

}
