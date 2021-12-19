package com.study.controller;

import com.study.dto.UserResponse;
import com.study.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> create(@RequestHeader String kakaoToken) {
        return ResponseEntity.ok(userService.create(kakaoToken));
    }
}
