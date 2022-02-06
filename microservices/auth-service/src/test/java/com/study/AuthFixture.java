package com.study;

import com.study.domain.Auth;
import com.study.dto.TokenResponse;

public class AuthFixture {

    public static final String TEST_AUTHORIZATION = "bearer **" ;
    public static final String TEST_KAKAO_TOKEN = "kakaoToken";
    public static final String TEST_FCM_TOKEN = "fcmToken";
    public static final String TEST_AUTH_ACCESS_TOKEN = "accessToken";
    public static final String TEST_AUTH_REFRESH_TOKEN = "refreshToken";
    public static final Long TEST_EXPIRATION = 30L;

    public static final Auth TEST_AUTH
            = Auth.createAuth(1L, TEST_AUTH_REFRESH_TOKEN, TEST_EXPIRATION);

    public static final TokenResponse TEST_TOKEN_RESPONSE
            = new TokenResponse(TEST_AUTH_ACCESS_TOKEN, TEST_AUTH_REFRESH_TOKEN);

}
