package com.kakaotech.ott.ott.global.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${redis.sentinel.password}")
    private String password;

    @Value("${redis.sentinel.master}")
    private String masterName;

    @Value("${redis.sentinel.nodes}")
    private String node; // 여러 개면 배열로 받아도 됩니다

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SentinelServersConfig sentinelConfig = config.useSentinelServers()
                .setMasterName(masterName)
                .addSentinelAddress(node) // 여러 개면 반복 추가
                .setPassword(password)
                .setDatabase(0)
                .setCheckSentinelsList(false);

        return Redisson.create(config);
    }
}
