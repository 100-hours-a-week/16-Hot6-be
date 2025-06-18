package com.kakaotech.ott.ott.payment.application.serviceImpl;

import com.kakaotech.ott.ott.global.exception.CustomException;
import com.kakaotech.ott.ott.global.exception.ErrorCode;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItem;
import com.kakaotech.ott.ott.orderItem.domain.model.OrderItemStatus;
import com.kakaotech.ott.ott.orderItem.domain.repository.OrderItemRepository;
import com.kakaotech.ott.ott.payment.application.service.PaymentService;
import com.kakaotech.ott.ott.payment.domain.model.Payment;
import com.kakaotech.ott.ott.payment.domain.model.PaymentMethod;
import com.kakaotech.ott.ott.payment.domain.repository.PaymentRepository;
import com.kakaotech.ott.ott.payment.presentation.dto.request.PaymentRequestDto;
import com.kakaotech.ott.ott.payment.presentation.dto.response.PaymentResponseDto;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionReason;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointActionType;
import com.kakaotech.ott.ott.pointHistory.domain.model.PointHistory;
import com.kakaotech.ott.ott.pointHistory.domain.repository.PointHistoryRepository;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrder;
import com.kakaotech.ott.ott.productOrder.domain.model.ProductOrderStatus;
import com.kakaotech.ott.ott.productOrder.domain.repository.ProductOrderRepository;
import com.kakaotech.ott.ott.user.domain.model.User;
import com.kakaotech.ott.ott.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ProductOrderRepository productOrderRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto, Long userId, Long orderId) {

        User user = userRepository.findById(userId);

        PointHistory pointHistory = pointHistoryRepository.findLatestPointHistoryByUserId(userId);

        if (pointHistory.getBalanceAfter() < paymentRequestDto.getPoint()) {
            throw new CustomException(ErrorCode.INSUFFICIENT_POINT_BALANCE);
        }

        ProductOrder productOrder = productOrderRepository.findByIdAndUserIdToPayment(orderId, userId);

        if (paymentRequestDto.getPoint() < productOrder.getSubtotalAmount() - productOrder.getDiscountAmount()) {
            throw new CustomException(ErrorCode.PAYMENT_POINT_BALANCE);
        }

        int afterPaymentPoint = pointHistory.getBalanceAfter() - paymentRequestDto.getPoint();

        PointHistory afterPointHistory = PointHistory.createPointHistory(userId, paymentRequestDto.getPoint(),
                afterPaymentPoint, PointActionType.DEDUCT, PointActionReason.PRODUCT_PURCHASE);

        if (productOrder.getStatus().equals(ProductOrderStatus.PAID)) {

            throw new CustomException(ErrorCode.ALREADY_PAID);
        }

        if (!productOrder.getStatus().equals(ProductOrderStatus.PENDING)) {

            throw new CustomException(ErrorCode.NOT_PENDING_STATE);
        }

        Payment payment = Payment.createPayment(orderId, PaymentMethod.POINT, paymentRequestDto.getPoint());

        Payment savedPayment = paymentRepository.save(payment, user);

        PointHistory savedPointHistory = pointHistoryRepository.save(afterPointHistory, user);

        productOrder.pay();
        productOrderRepository.paymentOrder(productOrder);

        List<OrderItem> orderItemList = orderItemRepository.findByProductOrderId(productOrder.getId());

        for(OrderItem orderItem : orderItemList) {
            if (orderItem.getStatus().equals(OrderItemStatus.PENDING))
                orderItem.pay();
        }

        orderItemRepository.payOrderItem(orderItemList);

        return new PaymentResponseDto(savedPointHistory.getId(), productOrder.getId(), savedPayment.getPaymentAmount(), savedPayment.getPaidAt());
    }
}
