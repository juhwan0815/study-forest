package com.study.controller;

import com.study.config.LoginUser;
import com.study.dto.UserResponse;
import com.study.dto.UserUpdateRequest;
import com.study.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

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

    @PatchMapping("/users/image")
    public ResponseEntity<UserResponse> updateImage(@LoginUser Long userId,
                                                    @RequestPart(required = false) MultipartFile image) {
        return ResponseEntity.ok(userService.updateImage(userId, image));
    }

    @PatchMapping("/users/profile")
    public ResponseEntity<UserResponse> updateProfile(@LoginUser Long userId,
                                                      @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }

    @GetMapping("/users/profile")
    public ResponseEntity<UserResponse> findByLoginId(@LoginUser Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @DeleteMapping("/users")
    public ResponseEntity<Void> delete(@LoginUser Long userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
