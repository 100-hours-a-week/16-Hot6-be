package com.kakaotech.ott.ott.user.repository;

import com.kakaotech.ott.ott.user.domain.OAuthToken;

public interface OAuthTokenRepository {

    OAuthToken save(OAuthToken oAuthToken);
}
