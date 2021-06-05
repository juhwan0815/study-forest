package com.study.authservice.model;

import lombok.Data;

@Data
public class KakaoProfile {

    private Long id;

    private Properties properties;

    @Data
    public static class Properties {
        private String nickname;

        private String profile_image;

        private String thumbnail_image;
    }
}
