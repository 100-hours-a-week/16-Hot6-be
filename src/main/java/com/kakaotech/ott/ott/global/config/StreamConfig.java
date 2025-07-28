package com.kakaotech.ott.ott.global.config;

public class StreamConfig {
    private final String streamKey;
    private final String group;
    private final String retryConsumer;

    public StreamConfig(String streamKey, String group, String retryConsumer) {
        this.streamKey = streamKey;
        this.group = group;
        this.retryConsumer = retryConsumer;
    }

    public String getStreamKey() {
        return streamKey;
    }

    public String getGroup() {
        return group;
    }

    public String getRetryConsumer() {
        return retryConsumer;
    }
}
