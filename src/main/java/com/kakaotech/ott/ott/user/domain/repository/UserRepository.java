package com.kakaotech.ott.ott.user.domain.repository;

import com.kakaotech.ott.ott.user.domain.model.User;

public interface UserRepository {

    User findById(Long userId);

    User update(User user);

    void delete(User user);

    void certify(User user);

    boolean existsByNicknameCommunity(String nicknameCommunity);
}
