package com.kakaotech.ott.ott.batch.application.component;

import com.kakaotech.ott.ott.batch.domain.model.BatchJobLog;
import com.kakaotech.ott.ott.batch.domain.repository.BatchJobLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class BatchExecutor {

    private final BatchJobLogRepository batchJobLogRepository;

    public void execute(String jobName, Runnable action) {
        LocalDateTime scheduleAt = LocalDateTime.now().withSecond(0).withNano(0);
        BatchJobLog log = BatchJobLog.createBatchJobLog(jobName, scheduleAt);
        BatchJobLog savedLog = batchJobLogRepository.save(log);

        try {
            action.run();
            savedLog.markSuccess();
        } catch (Exception e) {
            savedLog.markFailed(e.getMessage());
        } finally {
            batchJobLogRepository.update(savedLog);
        }
    }

}
