package com.study.authservice.client;

import com.study.authservice.model.common.KakaoProfile;

public interface KakaoServiceClient {

    KakaoProfile getKakaoProfile(String kakaoToken);
}
