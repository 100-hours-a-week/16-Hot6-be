package com.kakaotech.ott.ott.productOrder.infrastructure.repositoryImpl;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderJpaRepository;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import com.kakaotech.ott.ott.productOrder.infrastructure.entity.ProductOrderEntity;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.infrastructure.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ProductOrderRepositoryImpl implements ProductOrderRepository {

    private final ProductOrderJpaRepository productOrderJpaRepository;

    @Override
    @Transactional
    public ProductOrder save(ProductOrder productOrder, User user) {

        ProductOrderEntity productOrderEntity = ProductOrderEntity.from(productOrder, UserEntity.from(user));

        return productOrderJpaRepository.save(productOrderEntity).toDomain();
    }
}
