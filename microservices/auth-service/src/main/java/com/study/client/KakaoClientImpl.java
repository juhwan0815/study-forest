package com.study.client;

import com.google.gson.Gson;
import com.study.dto.KakaoProfile;
import com.study.exception.KakaoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoClientImpl implements KakaoClient {

    private final RestTemplate restTemplate;
    private final Gson gson;

    @Value("${social.kakao.url.profile}")
    private String kakaoProfileUrl;

    @Override
    public KakaoProfile getKakaoProfile(String kakaoToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + kakaoToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(kakaoProfileUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return gson.fromJson(response.getBody(), KakaoProfile.class);
        } else {
            throw new KakaoException("");
        }
    }
}
