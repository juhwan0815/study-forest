package com.study.client;

import com.google.gson.Gson;
import com.study.exception.NetworkException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoClientImpl implements KakaoClient {

    private final RestTemplate restTemplate;
    private final Gson gson;

    public static final String KAKAO_PROFILE_URL = "https://kapi.kakao.com/v2/user/me";

    public KakaoClientImpl(RestTemplateBuilder restTemplateBuilder, Gson gson) {
        this.restTemplate = restTemplateBuilder.build();
        this.gson = gson;
    }

    @Override
    public KakaoProfile getKakaoProfile(String kakaoToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_PROFILE_URL, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return gson.fromJson(response.getBody(), KakaoProfile.class);
        } else {
            throw new NetworkException("카카오 서버와 통신 중 오류가 발생했습니다.");
        }
    }
}
