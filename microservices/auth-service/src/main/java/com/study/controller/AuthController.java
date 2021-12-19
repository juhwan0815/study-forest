package com.study.controller;

import com.study.client.KakaoClient;
import com.study.dto.TokenResponse;
import com.study.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity auth(@RequestHeader String kakaoToken, @RequestHeader String fcmToken) {

        TokenResponse token = authService.login(kakaoToken, fcmToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken", token.getAccessToken())
                .header("refreshToken", token.getRefreshToken())
                .build();
    }


}
