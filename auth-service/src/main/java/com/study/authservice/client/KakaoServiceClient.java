package com.study.authservice.client;

import com.study.authservice.model.KakaoProfile;

public interface KakaoServiceClient {

    KakaoProfile getKakaoProfile(String kakaoToken);
}
