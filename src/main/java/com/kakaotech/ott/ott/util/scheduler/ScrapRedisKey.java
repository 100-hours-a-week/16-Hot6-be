package com.kakaotech.ott.ott.util.scheduler;

public class ScrapRedisKey {
        public static String postSetKey(Long postId) {
            return "scrap:set:post:" + postId;
        }

        public static String productSetKey(Long productId) {
            return "scrap:set:product:" + productId;
        }

        public static String serviceProductSetKey(Long spId) {
            return "scrap:set:service_product:" + spId;
        }

        // Stream 용 키
        public static final String SCRAP_STREAM_KEY_POST            = "scrap:stream:post:events";
        public static final String SCRAP_STREAM_KEY_PRODUCT         = "scrap:stream:product:events";
        public static final String SCRAP_STREAM_KEY_SERVICE_PRODUCT = "scrap:stream:service_product:events";
}
