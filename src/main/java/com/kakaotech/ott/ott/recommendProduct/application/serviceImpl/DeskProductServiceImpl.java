package com.kakaotech.ott.ott.recommendProduct.application.serviceImpl;

import com.kakaotech.ott.ott.recommendProduct.application.component.ClickCountAggregator;
import com.kakaotech.ott.ott.recommendProduct.application.service.DeskProductService;
import com.kakaotech.ott.ott.recommendProduct.domain.model.DeskProduct;
import com.kakaotech.ott.ott.recommendProduct.domain.model.ProductSubCategory;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.DeskProductRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.ProductSubCategoryRepository;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.DeskProductDetailResponseDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.DeskProductListResponseDto;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeskProductServiceImpl implements DeskProductService {

    private final DeskProductRepository deskProductRepository;
    private final ScrapRepository scrapRepository;
    private final ProductSubCategoryRepository productSubCategoryRepository;
    private final ClickCountAggregator clickCountAggregator;

    @Override
    @Transactional(readOnly = true)
    public DeskProductListResponseDto getDeskProducts(Long userId, Double lastWeight, Long lastRecommendedProductId, int size) {

        Slice<DeskProduct> deskProducts = deskProductRepository.findDeskProductsByWeight(lastWeight, lastRecommendedProductId, size);

        List<DeskProductListResponseDto.DeskProducts> deskProductsList = deskProducts.stream()
                .map(deskProduct -> {

                    ProductSubCategory productSubCategory = productSubCategoryRepository.findById(deskProduct.getSubCategoryId());

                    boolean scrapped = (userId != null) && scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.PRODUCT, deskProduct.getId());

                    return new DeskProductListResponseDto.DeskProducts(
                            deskProduct.getId(),
                            deskProduct.getName(),
                            productSubCategory.getName(),
                            deskProduct.getPrice(),
                            scrapped,
                            deskProduct.getWeight(),
                            deskProduct.getImagePath()
                    );
                })
                .toList();

        boolean hasNext = deskProductsList.size() == size;
        Long nextLastId = hasNext ? deskProductsList.get(deskProductsList.size() - 1).getProductId() : null;
        Double nextLastWeightCount = hasNext ? deskProductsList.get(deskProductsList.size() - 1).getWeight() : null;

        return new DeskProductListResponseDto(deskProductsList, new DeskProductListResponseDto.Pagination(size, nextLastWeightCount, nextLastId, hasNext));
    }

    @Override
    @Transactional
    public DeskProductDetailResponseDto getDeskProduct(Long userId, Long deskProductId) {

        clickCountAggregator.increment(deskProductId);

        DeskProduct deskProduct = deskProductRepository.findById(deskProductId);
        ProductSubCategory productSubCategory = productSubCategoryRepository.findById(deskProduct.getSubCategoryId());
        boolean scrapped = (userId != null) && scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.PRODUCT, deskProduct.getId());

        return new DeskProductDetailResponseDto(
                deskProduct.getId(),
                deskProduct.getName(),
                deskProduct.getPrice(),
                productSubCategory.getName(),
                deskProduct.getPurchasePlace(),
                deskProduct.getImagePath(),
                deskProduct.getPurchaseUrl(),
                scrapped);
    }
}
