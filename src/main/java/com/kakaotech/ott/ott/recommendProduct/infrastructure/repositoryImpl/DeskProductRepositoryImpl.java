package com.kakaotech.ott.ott.recommendProduct.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.recommendProduct.domain.model.DeskProduct;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.DeskProductJpaRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.DeskProductRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.ProductSubCategoryJpaRepository;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.DeskProductEntity;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.ProductSubCategoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DeskProductRepositoryImpl implements DeskProductRepository {

    private final DeskProductJpaRepository deskProductJpaRepository;
    private final ProductSubCategoryJpaRepository productSubCategoryJpaRepository;

    @Override
    public DeskProduct save(DeskProduct deskProduct,
                                  AiImageEntity aiImageEntity) {
        ProductSubCategoryEntity productSubCategoryEntity = productSubCategoryJpaRepository.findById(deskProduct.getSubCategoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.SUB_CATEGORY_NOT_FOUND));

        return deskProductJpaRepository.save(DeskProductEntity.from(deskProduct, productSubCategoryEntity, aiImageEntity))
                .toDomain();
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

    @Override
    @Transactional
    public void incrementScrapCount(Long targetId, Long delta) {

        DeskProductEntity deskProductEntity = deskProductJpaRepository.findById(targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.AI_PRODUCT_NOT_FOUND));

        if(deskProductEntity.getScrapCount() + delta < 0)
            return;

        deskProductJpaRepository.incrementScrapCount(targetId, delta);
    }

    @Override
    public DeskProduct findById(Long deskProductId) {

        return deskProductJpaRepository.findById(deskProductId)
                .orElseThrow(() -> new CustomException(ErrorCode.DESK_PRODUCT_NOT_FOUND))
                .toDomain();
    }

    @Override
    public Slice<DeskProduct> findDeskProductsByWeight(Double lastWeight, Long lastDeskProductId, int size) {
        Slice<DeskProductEntity> slice = deskProductJpaRepository.findAllDeskProductsByWeight(lastWeight, lastDeskProductId, PageRequest.of(0, size));

        return slice.map(DeskProductEntity::toDomain);
    }

    @Override
    public void batchUpdateWeights() {

        deskProductJpaRepository.batchUpdateWeights();
    }

    @Override
    public void incrementClickCount(Long deskProductId, Long delta) {

        DeskProductEntity deskProductEntity = deskProductJpaRepository.findById(deskProductId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if(deskProductEntity.getClickCount() + delta < 0)
            return;

        deskProductJpaRepository.incrementClickCount(deskProductId, delta);
    }

    @Override
    public boolean existsByProductCode(String productCode) {

        return deskProductJpaRepository.existsByProductCode(productCode);
    }

    @Override
    public DeskProduct findByProductCode(String productCode) {

        return deskProductJpaRepository.findByProductCode(productCode)
                .orElseThrow(() -> new CustomException(ErrorCode.DESK_PRODUCT_NOT_FOUND))
                .toDomain();
    }

    @Override
    public List<DeskProduct> findByProductCodeIn(List<String> productCodes) {
        return deskProductJpaRepository.findByProductCodeIn(productCodes)
                .stream()
                .map(DeskProductEntity::toDomain)
                .toList();
    }
}
