package com.kakaotech.ott.ott.product.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.product.domain.model.DeskProduct;
import com.kakaotech.ott.ott.product.domain.repository.DeskProductJpaRepository;
import com.kakaotech.ott.ott.product.domain.repository.DeskProductRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.DeskProductEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductSubCategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DeskProductRepositoryImpl implements DeskProductRepository {

    private final DeskProductJpaRepository deskProductJpaRepository;

    @Override
    public DeskProductEntity save(DeskProduct deskProduct, ProductSubCategoryEntity productSubCategoryEntity,
                                  AiImageEntity aiImageEntity) {

        return deskProductJpaRepository.save(DeskProductEntity.from(deskProduct, productSubCategoryEntity, aiImageEntity));
    }

    @Override
    public List<DeskProductEntity> findByAiImageId(Long aiImageId) {
        return deskProductJpaRepository.findByAiImageEntity_Id(aiImageId);
    }

    @Override
    public List<DeskProduct> findTop7ByWeight() {
        // DB에서 직접 weight 기준 상위 7개 조회
        List<DeskProductEntity> entities = deskProductJpaRepository.findByOrderByWeightDesc(PageRequest.of(0, 7));

        // Entity → Domain 변환
        return entities.stream()
                .map(DeskProductEntity::toDomain)
                .collect(Collectors.toList());
    }
}
