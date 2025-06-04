package com.kakaotech.ott.ott.productOrder.application.serviceimpl;

import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.productOrder.application.service.ProductOrderService;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStaus;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ProductOrderRequestDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.request.ServiceProductDto;
import com.kakaotech.ott.ott.productOrder.presentation.dto.response.ProductOrderResponseDto;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductOrderRepository productOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;


    @Override
    public ProductOrderResponseDto create(ProductOrderRequestDto productOrderRequestDto, Long userId) {

        User user = userRepository.findById(userId);

        List<OrderItem> orderItems = new ArrayList<>();
        int totalAmount = 0;
        int discountAmount = 0;

        for(ServiceProductDto serviceProduct : productOrderRequestDto.getProduct()) {
            Long productId = serviceProduct.getProductId();
            int price = serviceProduct.getPrice();
            int quantity = serviceProduct.getQuantity();

            // 주문한 상품 총액 계산
            totalAmount += price * quantity;
            // 할인 금액 계산
            //discountAmount += ???

            orderItemRepository.existsByProductIdAndPendingProductStatus(productId, OrderItemStatus.PENDING);

            OrderItem orderItem = OrderItem.createOrderItem(null, productId, price, quantity);
            orderItems.add(orderItem);
        }

        // 주문 생성
        ProductOrder productOrder = ProductOrder.createOrder(userId, totalAmount, 0);
        ProductOrder savedProductOrder = productOrderRepository.save(productOrder, user);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(savedProductOrder.getId());
            orderItemRepository.save(orderItem);
        }

        return new ProductOrderResponseDto(
                savedProductOrder.getId(),
                productOrderRequestDto.getProduct(),
                totalAmount,
                ProductOrderStaus.ORDERED,
                savedProductOrder.getOrderedAt());
    }
}
