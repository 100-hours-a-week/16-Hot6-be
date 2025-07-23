package com.kakaotech.ott.ott.like.application.serviceImpl;

import com.kakaotech.ott.ott.like.application.service.LikeService;
import com.kakaotech.ott.ott.like.presentation.dto.request.LikeRequestDto;
import com.kakaotech.ott.ott.util.scheduler.LikeRedisKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void toggleLike(Long userId, LikeRequestDto dto) {
        Long postId = dto.getPostId();
        String user = userId.toString();
        String setKey = LikeRedisKey.setLikeKey(postId);

        boolean isLiked = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(setKey, user));
        String action = isLiked ? "unlike" : "like";

        if (isLiked) {
            redisTemplate.opsForSet().remove(setKey, user);
        } else {
            redisTemplate.opsForSet().add(setKey, user);
        }

        Map<String, String> ev = Map.of(
                "userId", user,
                "postId", postId.toString(),
                "action", action,
                "ts", String.valueOf(System.currentTimeMillis())
        );
        redisTemplate.opsForStream().add(LikeRedisKey.LIKE_STREAM_KEY, ev);

    }
}
