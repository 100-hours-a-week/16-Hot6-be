package com.kakaotech.ott.ott.scrap.application.serviceImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.scrap.application.service.ScrapService;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.presentation.dto.request.ScrapRequestDto;
import com.kakaotech.ott.ott.util.scheduler.ScrapRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void toggleScrap(Long userId, ScrapRequestDto scrapRequestDto) {
        Long targetId   = scrapRequestDto.getTargetId();
        String user = userId.toString();
        String setKey = setScrapKey(scrapRequestDto.getType(), targetId);

        boolean isScrapped = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(setKey, user));
        String action = isScrapped ? "unscrap" : "scrap";

        if (isScrapped) {
            redisTemplate.opsForSet().remove(setKey, user);
        } else {
            redisTemplate.opsForSet().add(setKey, user);
        }

        String streamKey = streamScrapKey(scrapRequestDto.getType());

        Map<String, String> ev = Map.of(
                "userId", user,
                "targetId", targetId.toString(),
                "type", scrapRequestDto.getType().name(),
                "action", action,
                "ts", String.valueOf(System.currentTimeMillis())
        );
        redisTemplate.opsForStream().add(streamKey, ev);

    }

    private String setScrapKey(ScrapType type, Long targetId) {
        String setKey;
        switch (type) {
            case POST:
                setKey = ScrapRedisKey.postSetKey(targetId);
                break;
            case PRODUCT:
                setKey = ScrapRedisKey.productSetKey(targetId);
                break;
            case SERVICE_PRODUCT:
                setKey = ScrapRedisKey.serviceProductSetKey(targetId);
                break;
            default:
                throw new CustomException(ErrorCode.NOT_SCRAP_TYPE);
        }

        return setKey;
    }

    private String streamScrapKey(ScrapType type) {
        String streamKey;
        switch (type) {
            case POST:
                streamKey = ScrapRedisKey.SCRAP_STREAM_KEY_POST;
                break;
            case PRODUCT:
                streamKey = ScrapRedisKey.SCRAP_STREAM_KEY_PRODUCT;
                break;
            case SERVICE_PRODUCT:
                streamKey = ScrapRedisKey.SCRAP_STREAM_KEY_SERVICE_PRODUCT;
                break;
            default:
                throw new CustomException(ErrorCode.NOT_SCRAP_TYPE);
        }

        return streamKey;
    }
}
