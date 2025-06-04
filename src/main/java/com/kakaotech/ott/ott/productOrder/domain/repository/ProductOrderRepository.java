package com.kakaotech.ott.ott.productOrder.domain.repository;

import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.user.domain.model.User;

public interface ProductOrderRepository {

    ProductOrder save(ProductOrder productOrder, User user);
}
