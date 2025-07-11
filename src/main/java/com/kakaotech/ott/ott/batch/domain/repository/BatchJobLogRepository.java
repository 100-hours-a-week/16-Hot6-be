package com.kakaotech.ott.ott.batch.domain.repository;

import com.kakaotech.ott.ott.batch.domain.model.BatchJobLog;

import java.util.Optional;

public interface BatchJobLogRepository {

    Optional<BatchJobLog> findFailedOrPendingJob(String jobName);

    BatchJobLog save(BatchJobLog batchJobLog);

    void update(BatchJobLog batchJobLog);
}
