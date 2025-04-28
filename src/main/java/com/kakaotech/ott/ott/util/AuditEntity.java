package com.kakaotech.ott.ott.util;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // ✅ 이거 꼭 있어야 필드가 매핑됩니다
@EntityListeners(AuditingEntityListener.class) // ✅ 이거도 꼭 필요

public abstract class AuditEntity {

    @CreatedDate
    @Column(updatable = false, nullable = false) // 생성일은 업데이트되지 않음
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
