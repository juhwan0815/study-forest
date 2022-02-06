package com.study.controller;

import com.study.config.LoginUser;
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

    @PostMapping("/social/login")
    public ResponseEntity socialLogin(@RequestHeader String kakaoToken, @RequestHeader String fcmToken) {
        TokenResponse token = authService.login(kakaoToken, fcmToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken", token.getAccessToken())
                .header("refreshToken", token.getRefreshToken())
                .build();
    }

    @PostMapping("/refresh")
    public ResponseEntity refresh(@LoginUser Long userId, @RequestHeader("Authorization") String refreshToken){
        TokenResponse token = authService.refresh(userId, refreshToken.substring(7));

        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken", token.getAccessToken())
                .header("refreshToken", token.getRefreshToken())
                .build();

    }



}
