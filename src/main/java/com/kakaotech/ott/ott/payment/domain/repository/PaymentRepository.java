package com.kakaotech.ott.ott.payment.domain.repository;

import com.kakaotech.ott.ott.payment.domain.model.Payment;
import com.kakaotech.ott.ott.user.domain.model.User;

public interface PaymentRepository {

    Payment save(Payment payment, User user);

}
