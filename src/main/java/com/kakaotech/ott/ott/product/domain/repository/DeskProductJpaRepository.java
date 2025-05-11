package com.kakaotech.ott.ott.product.domain.repository;

import com.kakaotech.ott.ott.product.infrastructure.entity.DeskProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeskProductJpaRepository extends JpaRepository<DeskProductEntity, Long> {

    List<DeskProductEntity> findByAiImageEntity_Id(Long aiImageId);
}
