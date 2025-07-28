package com.kakaotech.ott.ott.util.scheduler;

public class LikeRedisKey {

    public static final String LIKE_SET_PREFIX    = "like:set:post:";
    public static final String LIKE_STREAM_KEY    = "like:stream:events";

    public static String setLikeKey(Long postId)    { return LIKE_SET_PREFIX   + postId; }

}