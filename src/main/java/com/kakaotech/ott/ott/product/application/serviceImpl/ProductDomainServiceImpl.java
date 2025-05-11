package com.kakaotech.ott.ott.product.application.serviceImpl;

import com.kakaotech.ott.ott.product.application.service.ProductDomainService;
import com.kakaotech.ott.ott.aiImage.domain.model.AiImage;
import com.kakaotech.ott.ott.product.domain.model.DeskProduct;
import com.kakaotech.ott.ott.product.domain.model.ProductMainCategory;
import com.kakaotech.ott.ott.product.domain.model.ProductSubCategory;
import com.kakaotech.ott.ott.aiImage.domain.repository.AiImageRepository;
import com.kakaotech.ott.ott.product.domain.repository.DeskProductRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductMainCategoryRepository;
import com.kakaotech.ott.ott.product.domain.repository.ProductSubCategoryRepository;
import com.kakaotech.ott.ott.aiImage.infrastructure.entity.AiImageEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductMainCategoryEntity;
import com.kakaotech.ott.ott.product.infrastructure.entity.ProductSubCategoryEntity;
import com.kakaotech.ott.ott.aiImage.presentation.dto.request.AiImageAndProductRequestDto;
import com.kakaotech.ott.ott.product.presentation.dto.request.ProductDetailRequestDto;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductDomainServiceImpl implements ProductDomainService {

    private final ProductMainCategoryRepository productMainCategoryRepository;
    private final ProductSubCategoryRepository productSubCategoryRepository;
    private final DeskProductRepository deskProductRepository;
    private final AiImageRepository aiImageRepository;
    private final UserRepository userRepository;

    @Override
    public List<DeskProduct> createdProduct(AiImageAndProductRequestDto aiImageAndProductRequestDto, AiImage aiImage, Long userId) {
        List<DeskProduct> savedDeskProducts = new ArrayList<>();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."))
                .toDomain();

        AiImageEntity aiImageEntity = aiImageRepository.findById(aiImage.getId())
                .orElseThrow(() -> new EntityNotFoundException("AI 이미지가 없습니다."));

        List<ProductDetailRequestDto> productList = aiImageAndProductRequestDto.getProducts();

        // 제품 리스트가 비어있으면 바로 빈 리스트 반환
        if (productList == null || productList.isEmpty()) {
            return savedDeskProducts;
        }

        for (ProductDetailRequestDto product : productList) {
            String mainCategoryName = product.getMainCategory();
            String subCategoryName = product.getSubCategory();

            ProductMainCategoryEntity productMainCategoryEntity = productMainCategoryRepository.findByName(mainCategoryName)
                    .orElseGet(() -> {
                        ProductMainCategory newMainCategory = ProductMainCategory.createProductMainCategory(mainCategoryName);
                        return productMainCategoryRepository.save(newMainCategory);
                    });

            ProductSubCategoryEntity productSubCategoryEntity = productSubCategoryRepository.findByName(subCategoryName)
                    .orElseGet(() -> {
                        ProductSubCategory newSubCategory = ProductSubCategory.createProductSubCategory(productMainCategoryEntity.getId(), subCategoryName);
                        return productSubCategoryRepository.save(newSubCategory, productMainCategoryEntity);
                    });

            DeskProduct deskProduct = DeskProduct.createDeskProduct(
                    productSubCategoryEntity.getId(), aiImage.getId(),
                    product.getName(), product.getPrice(), product.getPurchasePlace(),
                    product.getPurchaseUrl(), 0, 0, product.getImagePath()
            );

            DeskProduct generatedDeskProduct = deskProductRepository.save(deskProduct, productSubCategoryEntity, aiImageEntity).toDomain();
            savedDeskProducts.add(generatedDeskProduct);
        }

        return savedDeskProducts;
    }
}
