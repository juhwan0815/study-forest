package com.study.client;

import com.study.dto.KakaoProfile;

public interface KakaoClient {

    KakaoProfile getKakaoProfile(String kakaoToken);
}
