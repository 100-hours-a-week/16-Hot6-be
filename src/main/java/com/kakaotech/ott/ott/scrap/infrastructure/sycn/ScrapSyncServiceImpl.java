package com.kakaotech.ott.ott.scrap.infrastructure.sycn;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScrapSyncServiceImpl implements ScrapSyncService{

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void syncBatchWithVersion(List<Object[]> params, Map<String, Long> deltas) {
        String sql = """
                INSERT INTO scraps (user_id, type, target_id, is_active, last_event_id)
                VALUES (:userId, :type, :targetId, :scrapped, :eventId)
                ON DUPLICATE KEY UPDATE
                    is_active = IF(last_event_id < VALUES(last_event_id),
                                    VALUES(is_active),
                                    is_active),
                    last_event_id = IF(last_event_id < VALUES(last_event_id),
                                    VALUES(last_event_id),
                                    last_event_id)
                """;

        SqlParameterSource[] batch = params.stream()
                .map(p -> new MapSqlParameterSource()
                        .addValue("userId", p[0])
                        .addValue("type", p[1])
                        .addValue("targetId", p[2])
                        .addValue("scrapped", p[3])
                        .addValue("eventId", p[4]))
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }
}

