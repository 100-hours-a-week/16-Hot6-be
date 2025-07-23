package com.kakaotech.ott.ott.like.infrastructure.sync;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeSyncServiceImpl implements LikeSyncService{

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void syncBatchWithVersion(List<Object[]> params, Map<Long, Long> deltas) {
        String sql = """
        INSERT INTO likes (user_id, post_id, is_active, last_event_id)
            VALUES (:userId, :postId, :liked, :eventId)
            ON DUPLICATE KEY UPDATE
              is_active     = IF(last_event_id < VALUES(last_event_id),
                                 VALUES(is_active),
                                 is_active),
              last_event_id = IF(last_event_id < VALUES(last_event_id),
                                 VALUES(last_event_id),
                                 last_event_id)
        """;

        SqlParameterSource[] batch = params.stream()
                .map(p -> new MapSqlParameterSource()
                        .addValue("liked", p[2])
                        .addValue("eventId", p[4])
                        .addValue("userId", p[0])
                        .addValue("postId", p[1]))
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }
}
