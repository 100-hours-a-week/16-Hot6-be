package com.kakaotech.ott.ott.recommendProduct.application.serviceImpl;

import com.kakaotech.ott.ott.aiImage.presentation.dto.response.AiImageSaveResponseDto;
import com.kakaotech.ott.ott.global.cache.DistributedLock;
import com.kakaotech.ott.ott.global.config.RedisConfig;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.recommendProduct.application.service.ProductDomainService;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.recommendProduct.domain.model.AiImageRecommendedProduct;
import com.kakaotech.ott.ott.recommendProduct.domain.model.DeskProduct;
import com.kakaotech.ott.ott.recommendProduct.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.recommendProduct.domain.model.ProductSubCategory;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.AiImageRecommendedProductRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.DeskProductRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.ProductMainCategoryRepository;
import com.kakaotech.ott.ott.recommendProduct.domain.repository.ProductSubCategoryRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.request.ProductDetailRequestDto;
import com.kakaotech.ott.ott.recommendProduct.presentation.dto.response.RecommendedItemsDto;
import com.kakaotech.ott.ott.scrap.domain.model.ScrapType;
import com.kakaotech.ott.ott.scrap.domain.repository.ScrapRepository;
import com.kakaotech.ott.ott.user.domain.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDomainServiceImpl implements ProductDomainService {

    private final ProductMainCategoryRepository productMainCategoryRepository;
    private final ProductSubCategoryRepository productSubCategoryRepository;
    private final DeskProductRepository deskProductRepository;
    private final AiImageRepository aiImageRepository;
    private final UserAuthRepository userAuthRepository;
    private final ScrapRepository scrapRepository;
    private final AiImageRecommendedProductRepository aiImageRecommendedProductRepository;

//    @Override
//    @Transactional
//    public AiImageSaveResponseDto createdProduct(AiImageAndProductRequestDto aiImageAndProductRequestDto, AiImage aiImage, Long userId) {
//
//        User user = userAuthRepository.findById(userId);
//
//        AiImageEntity aiImageEntity = aiImageRepository.findById(aiImage.getId())
//                .orElseThrow(() -> new EntityNotFoundException("AI 이미지가 없습니다."));
//
//        List<ProductDetailRequestDto> productList = aiImageAndProductRequestDto.getProducts();
//
//        // 제품 리스트가 비어있으면 바로 빈 리스트 반환
//        if (productList == null || productList.isEmpty()) {
//            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
//        }
//
//        for (ProductDetailRequestDto product : productList) {
//
//            boolean existsProduct = deskProductRepository.existsByProductCode(product.getProductCode());
//
//            String mainCategoryName = product.getMainCategory();
//            String subCategoryName = product.getSubCategory();
//
//            ProductMainCategoryEntity productMainCategoryEntity = productMainCategoryRepository.findByName(mainCategoryName)
//                    .orElseGet(() -> {
//                        ProductMainCategory newMainCategory = ProductMainCategory.createProductMainCategory(mainCategoryName);
//                        return productMainCategoryRepository.save(newMainCategory);
//                    });
//
//            ProductSubCategoryEntity productSubCategoryEntity = productSubCategoryRepository.findByName(subCategoryName)
//                    .orElseGet(() -> {
//                        ProductSubCategory newSubCategory = ProductSubCategory.createProductSubCategory(productMainCategoryEntity.getId(), subCategoryName);
//                        return productSubCategoryRepository.save(newSubCategory, productMainCategoryEntity);
//                    });
//
//            AiImageRecommendedProduct aiImageRecommendedProduct;
//
//            if (!existsProduct) {
//                DeskProduct deskProduct = DeskProduct.createDeskProduct(
//                        productSubCategoryEntity.getId(), product.getProductCode(),
//                        product.getName(), product.getPrice(), product.getPurchasePlace(),
//                        product.getPurchaseUrl(), product.getImagePath()
//                );
//
//                DeskProduct generatedDeskProduct = deskProductRepository.save(deskProduct, productSubCategoryEntity, aiImageEntity);
//                aiImageRecommendedProduct = AiImageRecommendedProduct.createAiImageRecommendedProduct(aiImage.getId(), generatedDeskProduct.getId(), product.getCenterX(), product.getCenterY());
//            } else {
//                DeskProduct generatedDeskProduct = deskProductRepository.findByProductCode(product.getProductCode());
//                aiImageRecommendedProduct = AiImageRecommendedProduct.createAiImageRecommendedProduct(aiImage.getId(), generatedDeskProduct.getId(), product.getCenterX(), product.getCenterY());
//
//            }
//
//            aiImageRecommendedProductRepository.save(aiImageRecommendedProduct);
//
//        }
//
//        return new AiImageSaveResponseDto(aiImage.getId());
//    }

    @Override
    @Transactional
    public AiImageSaveResponseDto createdProduct(AiImageAndProductRequestDto aiImageAndProductRequestDto, AiImage aiImage, Long userId) {

        AiImageEntity aiImageEntity = validateUserAndImageExist(userId, aiImage.getId());

        List<ProductDetailRequestDto> productList = aiImageAndProductRequestDto.getProducts();

        if (productList == null || productList.isEmpty()) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Map<String, ProductMainCategory> mainCategoryMap = prepareMainCategoryMap(productList);
        Map<String, ProductSubCategory> subCategoryMap = prepareSubCategoryMap(productList);
        Map<String, DeskProduct> productMap = prepareProductMap(productList);

        for (ProductDetailRequestDto product : productList) {
            processProduct(product, mainCategoryMap, subCategoryMap, productMap, aiImageEntity, aiImage.getId());
        }

        return new AiImageSaveResponseDto(aiImage.getId());

    }

    @Override
    @Transactional(readOnly = true)
    public List<RecommendedItemsDto> getRecommendItems(Long userId) {
        return deskProductRepository.findTop7ByWeight().stream()
                .map(deskProduct -> new RecommendedItemsDto(
                        deskProduct.getId(),
                        deskProduct.getName(),
                        deskProduct.getImagePath(),
                        deskProduct.getPurchaseUrl(),
                        deskProduct.getPurchasePlace(),
                        productSubCategoryRepository.findById(deskProduct.getSubCategoryId()).getName(),
                        (userId != null) && scrapRepository.existsByUserIdAndTypeAndPostId(userId, ScrapType.PRODUCT, deskProduct.getId())
                ))
                .collect(Collectors.toList());
    }

    private AiImageEntity validateUserAndImageExist(Long userId, Long aiImageId) {
        userAuthRepository.findById(userId);
        return aiImageRepository.findById(aiImageId)
                .orElseThrow(() -> new CustomException(ErrorCode.AIIMAGE_NOT_FOUND));
    }

    private Map<String, ProductMainCategory> prepareMainCategoryMap(List<ProductDetailRequestDto> products) {
        Set<String> names = products.stream().map(ProductDetailRequestDto::getMainCategory).collect(Collectors.toSet());
        return productMainCategoryRepository.findByNameIn(List.copyOf(names)).stream()
                .collect(Collectors.toMap(ProductMainCategory::getName, Function.identity()));
    }

    private Map<String, ProductSubCategory> prepareSubCategoryMap(List<ProductDetailRequestDto> products) {
        Set<String> names = products.stream().map(ProductDetailRequestDto::getSubCategory).collect(Collectors.toSet());
        return productSubCategoryRepository.findByNameIn(List.copyOf(names)).stream()
                .collect(Collectors.toMap(ProductSubCategory::getName, Function.identity()));
    }

    private Map<String, DeskProduct> prepareProductMap(List<ProductDetailRequestDto> products) {
        Set<String> codes = products.stream().map(ProductDetailRequestDto::getProductCode).collect(Collectors.toSet());
        return deskProductRepository.findByProductCodeIn(List.copyOf(codes)).stream()
                .collect(Collectors.toMap(DeskProduct::getProductCode, Function.identity()));
    }

    private void processProduct(ProductDetailRequestDto product,
                                Map<String, ProductMainCategory> mainMap,
                                Map<String, ProductSubCategory> subMap,
                                Map<String, DeskProduct> productMap,
                                AiImageEntity aiImageEntity,
                                Long aiImageId) {

        ProductMainCategory mainCategory = mainMap.computeIfAbsent(product.getMainCategory(), name ->
                productMainCategoryRepository.save(ProductMainCategory.createProductMainCategory(name)));

        ProductSubCategory subCategory = subMap.computeIfAbsent(product.getSubCategory(), name ->
                productSubCategoryRepository.save(
                        ProductSubCategory.createProductSubCategory(mainCategory.getId(), name)));

        DeskProduct deskProduct = productMap.computeIfAbsent(product.getProductCode(), code -> {
            DeskProduct newProduct = DeskProduct.createDeskProduct(
                    subCategory.getId(), product.getProductCode(),
                    product.getName(), product.getPrice(),
                    product.getPurchasePlace(), product.getPurchaseUrl(),
                    product.getImagePath()
            );
            return deskProductRepository.save(newProduct, aiImageEntity);
        });

        AiImageRecommendedProduct recommend = AiImageRecommendedProduct.createAiImageRecommendedProduct(
                aiImageId, deskProduct.getId(), product.getCenterX(), product.getCenterY());

        aiImageRecommendedProductRepository.save(recommend);
    }

}
