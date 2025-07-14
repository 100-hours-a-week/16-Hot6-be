package com.kakaotech.ott.ott.batch.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.batch.domain.model.BatchJobLog;
import com.kakaotech.ott.ott.batch.domain.repository.BatchJobLogJpaRepository;
import com.kakaotech.ott.ott.batch.domain.repository.BatchJobLogRepository;
import com.kakaotech.ott.ott.batch.infrastructure.entity.BatchJobLogEntity;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BatchJobLogRepositoryImpl implements BatchJobLogRepository {

    private final BatchJobLogJpaRepository batchJobLogJpaRepository;

    @Override
    public Optional<BatchJobLog> findFailedOrPendingJob(String jobName) {
        return batchJobLogJpaRepository.findFailedOrPendingJob(jobName).map(BatchJobLogEntity::toDomain);
    }

    @Override
    public BatchJobLog save(BatchJobLog batchJobLog) {
        BatchJobLogEntity batchJobLogEntity = BatchJobLogEntity.from(batchJobLog);
        return batchJobLogJpaRepository.save(batchJobLogEntity).toDomain();
    }

    @Override
    public void update(BatchJobLog batchJobLog) {
        BatchJobLogEntity batchJobLogEntity = batchJobLogJpaRepository.findById(batchJobLog.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.BATCH_NOT_FOUND));

        batchJobLogEntity.updateFrom(batchJobLog);
    }
}
