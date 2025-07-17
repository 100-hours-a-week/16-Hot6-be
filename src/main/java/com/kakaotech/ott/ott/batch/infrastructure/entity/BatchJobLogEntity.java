package com.kakaotech.ott.ott.batch.infrastructure.entity;

import com.kakaotech.ott.ott.batch.domain.model.BatchJobLog;
import com.kakaotech.ott.ott.batch.domain.model.BatchJobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "batch_job_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BatchJobLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "scheduled_time", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BatchJobStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BatchJobLog toDomain() {
        return BatchJobLog.builder()
                .id(this.id)
                .jobName(this.jobName)
                .scheduledTime(this.scheduledTime)
                .startedAt(this.startedAt)
                .endedAt(this.endedAt)
                .status(this.status)
                .errorMessage(this.errorMessage)
                .createdAt(this.createdAt)
                .build();
    }

    public static BatchJobLogEntity from(BatchJobLog batchJobLog) {
        return BatchJobLogEntity.builder()
                .jobName(batchJobLog.getJobName())
                .scheduledTime(batchJobLog.getScheduledTime())
                .startedAt(batchJobLog.getStartedAt())
                .endedAt(batchJobLog.getEndedAt())
                .status(batchJobLog.getStatus())
                .errorMessage(batchJobLog.getErrorMessage())
                .build();
    }

    public void updateFrom(BatchJobLog log) {
        this.status = log.getStatus();
        this.endedAt = log.getEndedAt();
        this.errorMessage = log.getErrorMessage();
    }

}
