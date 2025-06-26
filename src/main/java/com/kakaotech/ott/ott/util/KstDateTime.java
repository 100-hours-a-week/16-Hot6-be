package com.kakaotech.ott.ott.util;

import com.fasterxml.jackson.annotation.JsonValue;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class KstDateTime {

    private final LocalDateTime utcDateTime;

    public KstDateTime(LocalDateTime utcDateTime) {
        this.utcDateTime = utcDateTime;
    }

    @JsonValue // 이걸 붙이면 JSON 응답에서 문자열로 바로 직렬화됨!
    public String toKstString() {
        return utcDateTime
                .atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
    }

    public LocalDateTime getUtcDateTime() {
        return utcDateTime;
    }
}

