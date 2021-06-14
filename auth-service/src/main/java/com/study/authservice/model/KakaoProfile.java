package com.study.authservice.model;

import lombok.Data;

@Data
public class KakaoProfile {

    private Long id;

    private Properties properties;

    private KakaoAccount kakao_account;

    @Data
    public static class Properties {
        private String nickname;

        private String profile_image;

        private String thumbnail_image;
    }

    @Data
    public static class KakaoAccount{
        private String age_range;
        private String gender;
    }

}
