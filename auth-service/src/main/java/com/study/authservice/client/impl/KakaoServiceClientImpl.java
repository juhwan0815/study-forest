package com.study.authservice.client.impl;

import com.google.gson.Gson;
import com.study.authservice.client.KakaoServiceClient;
import com.study.authservice.exception.KakaoException;
import com.study.authservice.model.KakaoProfile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoServiceClientImpl implements KakaoServiceClient {

    private final RestTemplate restTemplate;
    private final Environment env;
    private final Gson gson;

    @Override
    public KakaoProfile getKakaoProfile(String kakaoToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization","Bearer " + kakaoToken);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(null, headers);

        try{
            ResponseEntity<String> response = restTemplate.postForEntity(env.getProperty("social.kakao.url.profile"), request, String.class);

            if(response.getStatusCode() == HttpStatus.OK) {
                return gson.fromJson(response.getBody(), KakaoProfile.class);
            }

        } catch (Exception e){
            throw new KakaoException();
        }
        throw new KakaoException();
    }
}
