package com.kakaotech.ott.ott.scrap.infrastructure.sycn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
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

        for (Map.Entry<String, Long> entry : deltas.entrySet()) {
            String[] parts = entry.getKey().split(":"); // key = "POST:123" → [POST, 123]
            String type = parts[0];
            Long targetId = Long.parseLong(parts[1]);
            Long delta = entry.getValue();

            String updateSql;
            MapSqlParameterSource param = new MapSqlParameterSource()
                    .addValue("delta", delta)
                    .addValue("targetId", targetId);

            switch (type) {
                case "POST" -> updateSql = """
                UPDATE posts
                   SET scrap_count = CASE
                                        WHEN scrap_count + :delta < 0 THEN 0
                                        ELSE scrap_count + :delta
                                     END
                 WHERE id = :targetId
            """;

                case "PRODUCT" -> updateSql = """
                UPDATE desk_products
                   SET scrap_count = CASE
                                        WHEN scrap_count + :delta < 0 THEN 0
                                        ELSE scrap_count + :delta
                                     END
                 WHERE id = :targetId
            """;

                case "SERVICE_PRODUCT" -> updateSql = """
                UPDATE service_products
                   SET scrap_count = CASE
                                        WHEN scrap_count + :delta < 0 THEN 0
                                        ELSE scrap_count + :delta
                                     END
                 WHERE id = :targetId
            """;

                default -> {
                    log.warn("⚠️ Unknown scrap type: {}", type);
                    continue;
                }
            }

            namedParameterJdbcTemplate.update(updateSql, param);
        }
    }
}

