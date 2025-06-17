package com.kakaotech.ott.ott.recommendProduct.domain.repository;

import com.kakaotech.ott.ott.post.domain.model.Post;
import com.kakaotech.ott.ott.recommendProduct.domain.model.DeskProduct;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.recommendProduct.infrastructure.entity.ProductSubCategoryEntity;
import org.springframework.data.domain.Slice;

import java.util.List;


public interface DeskProductRepository {

    DeskProduct save(DeskProduct deskProduct, ProductSubCategoryEntity productSubCategoryEntity, AiImageEntity aiImageEntity);

    List<DeskProduct> findTop7ByWeight();

    void incrementScrapCount(Long postId, Long delta);

    DeskProduct findById(Long deskProductId);

    Slice<DeskProduct> findDeskProductsByWeight(Double lastWeight, Long lastDeskProductId, int size);
}
