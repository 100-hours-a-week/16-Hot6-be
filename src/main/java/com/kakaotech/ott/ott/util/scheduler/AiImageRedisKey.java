package com.kakaotech.ott.ott.util.scheduler;

public class AiImageRedisKey {
    // AI 이미지 처리 요청 스트림
    public static final String ORIGINAL_IMAGES_STREAM = "original:images";

    // AI 이미지 처리 완료 스트림
    public static final String COMPLETED_IMAGES_STREAM = "completed:images";

    // Consumer Group 이름
    public static final String TO_AI_GROUP = "to-ai-group";
    public static final String TO_BE_GROUP = "to-be-group";
}
