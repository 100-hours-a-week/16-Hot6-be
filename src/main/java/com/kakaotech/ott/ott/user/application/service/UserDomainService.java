package com.kakaotech.ott.ott.user.application.service;

import com.kakaotech.ott.ott.user.domain.model.User;

import java.util.Map;

public interface UserDomainService {

    User saveOrGetKakaoUser(Map<String, Object> attributes);
}
