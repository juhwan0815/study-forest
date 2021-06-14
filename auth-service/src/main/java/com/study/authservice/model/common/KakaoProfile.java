package com.study.authservice.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoProfile {

    private Long id;

    private Properties properties;

    private KakaoAccount kakao_account;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Properties {
        private String nickname;

        private String profile_image;

        private String thumbnail_image;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoAccount{
        private String age_range;
        private String gender;
    }

}
