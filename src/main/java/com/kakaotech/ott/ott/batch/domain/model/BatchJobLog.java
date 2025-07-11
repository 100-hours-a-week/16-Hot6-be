package com.kakaotech.ott.ott.batch.domain.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatchJobLog {

    private Long id;

    private String jobName;

    private LocalDateTime scheduledTime;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private BatchJobStatus status;

    private String errorMessage;

    private LocalDateTime createdAt;

    public static BatchJobLog createBatchJobLog(String jobName, LocalDateTime scheduledTime) {

        return BatchJobLog.builder()
                .jobName(jobName)
                .scheduledTime(scheduledTime)
                .startedAt(LocalDateTime.now())
                .status(BatchJobStatus.RUNNING)
                .build();
    }

    public void markSuccess() {
        this.endedAt = LocalDateTime.now();
        this.status = BatchJobStatus.SUCCESS;
    }

    public void markFailed(String message) {
        this.endedAt = LocalDateTime.now();
        this.status = BatchJobStatus.FAILED;
        this.errorMessage = message;
    }
}
