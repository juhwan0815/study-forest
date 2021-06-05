package com.study.authservice.controller;

import com.study.authservice.client.UserServiceClient;
import com.study.authservice.model.CreateTokenResult;
import com.study.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity login(@RequestHeader String kakaoToken){

        CreateTokenResult createTokenResult = authService.login(kakaoToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken",createTokenResult.getAccessToken())
                .header("refreshToken", createTokenResult.getRefreshToken())
                .build();
    }


}
