package com.kakaotech.ott.ott.batch.domain.repository;

import com.kakaotech.ott.ott.batch.infrastructure.entity.BatchJobLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BatchJobLogJpaRepository extends JpaRepository<BatchJobLogEntity, Long> {
    @Query("SELECT b FROM BatchJobLogEntity b WHERE b.jobName = :jobName AND b.status IN ('FAILED', 'PENDING') ORDER BY b.scheduledTime DESC")
    Optional<BatchJobLogEntity> findFailedOrPendingJob(@Param("jobName") String jobName);
}
