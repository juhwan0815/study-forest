package com.study.controller;

import com.study.dto.UserResponse;
import com.study.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> create(@RequestHeader String kakaoToken) {
        return ResponseEntity.ok(userService.create(kakaoToken));
    }

    @PostMapping("/users/{kakaoId}")
    public ResponseEntity<UserResponse> login(@PathVariable Long kakaoId, @RequestHeader String fcmToken) {
        return ResponseEntity.ok(userService.findByKakaoId(kakaoId, fcmToken));
    }

}
