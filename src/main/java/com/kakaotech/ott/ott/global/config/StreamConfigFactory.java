package com.kakaotech.ott.ott.global.config;

import com.kakaotech.ott.ott.util.scheduler.ScrapRedisKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StreamConfigFactory {

    @Bean("postStreamConfig")
    public StreamConfig postStreamConfig() {
        return new StreamConfig(
                ScrapRedisKey.SCRAP_STREAM_KEY_POST,
                "scrapPostGroup",
                "scrapPostRetryConsumer"
        );
    }

    @Bean("productStreamConfig")
    public StreamConfig productStreamConfig() {
        return new StreamConfig(
                ScrapRedisKey.SCRAP_STREAM_KEY_PRODUCT,
                "scrapProductGroup",
                "scrapProductRetryConsumer"
        );
    }

    @Bean("serviceProductStreamConfig")
    public StreamConfig serviceProductStreamConfig() {
        return new StreamConfig(
                ScrapRedisKey.SCRAP_STREAM_KEY_SERVICE_PRODUCT,
                "scrapServiceProductGroup",
                "scrapServiceProductRetryConsumer"
        );
    }
}
