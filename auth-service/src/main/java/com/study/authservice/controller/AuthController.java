package com.study.authservice.controller;

import com.study.authservice.config.LoginUser;
import com.study.authservice.model.common.CreateTokenResult;
import com.study.authservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth")
    public ResponseEntity create(@RequestHeader String kakaoToken,
                                 @RequestHeader String fcmToken){

        CreateTokenResult createTokenResult = authService.login(kakaoToken,fcmToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken",createTokenResult.getAccessToken())
                .header("refreshToken", createTokenResult.getRefreshToken())
                .build();
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity refresh(@LoginUser Long userId,
                                  @RequestHeader(name = HttpHeaders.AUTHORIZATION) String refreshToken){
        return ResponseEntity.status(HttpStatus.OK)
                .header("accessToken",authService.refresh(refreshToken,userId))
                .build();
    }

    @DeleteMapping("/auth")
    public ResponseEntity delete(@LoginUser Long userId){
        authService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
