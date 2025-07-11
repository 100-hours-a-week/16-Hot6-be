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
        batchJobLogRepository.save(log);

        try {
            action.run();
            log.markSuccess();
        } catch (Exception e) {
            log.markFailed(e.getMessage());
        } finally {
            batchJobLogRepository.update(log);
        }
    }

}
