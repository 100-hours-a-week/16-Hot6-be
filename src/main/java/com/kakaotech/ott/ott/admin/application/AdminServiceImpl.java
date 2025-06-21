package com.kakaotech.ott.ott.admin.application;

import com.kakaotech.ott.ott.admin.presentation.dto.response.AdminProductStatusResponseDto;
import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.product.domain.model.ProductVariant;
import com.kakaotech.ott.ott.product.domain.repository.ProductVariantRepository;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import com.kakaotech.ott.ott.user.domain.model.Role;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductVariantRepository productVariantRepository;

    @Override
    public AdminProductStatusResponseDto getOrderProductStatus(Long userId) {

        User user = userRepository.findById(userId);

        if (user.getRole().equals(Role.USER))
            throw new CustomException(ErrorCode.USER_NOT_ADMIN);

        List<OrderItem> refundedProduct = orderItemRepository.findByStatus(OrderItemStatus.REFUND_REQUEST);
        List<OrderItem> paidProduct = orderItemRepository.findByStatus(OrderItemStatus.PAID);

        List<AdminProductStatusResponseDto.RefundedProduct> refundList = refundedProduct.stream()
                .map(product -> {

                    Long ordererId = productOrderRepository.findById(product.getOrderId()).getUserId();
                    User orderer = userRepository.findById(ordererId);
                    ProductVariant productVariant = productVariantRepository.findById(product.getVariantsId());

                    return new AdminProductStatusResponseDto.RefundedProduct(
                            product.getVariantsId(),
                            product.getPromotionId(),
                            orderer.getNicknameKakao(),
                            productVariant.getName(),
                            product.getQuantity(),
                            product.getFinalPrice(),
                            product.getRefundReason(),
                            product.getRefundedAt()
                    );
                })
                .toList();

        List<AdminProductStatusResponseDto.PaidProduct> paidList = paidProduct.stream()
                .map(product -> {
                    Long ordererId = productOrderRepository.findById(product.getOrderId()).getUserId();
                    User orderer = userRepository.findById(ordererId);
                    ProductVariant productVariant = productVariantRepository.findById(product.getVariantsId());

                    return new AdminProductStatusResponseDto.PaidProduct(
                            product.getVariantsId(),
                            product.getPromotionId(),
                            orderer.getNicknameKakao(),
                            productVariant.getName(),
                            product.getQuantity(),
                            product.getFinalPrice(),
                            productVariant.getCreatedAt()
                    );
                })
                .toList();

        return new AdminProductStatusResponseDto(refundList, paidList);
    }
}
