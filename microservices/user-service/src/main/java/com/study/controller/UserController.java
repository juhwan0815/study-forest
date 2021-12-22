package com.study.controller;

import com.study.config.LoginUser;
import com.study.dto.keyword.KeywordCreateRequest;
import com.study.dto.keyword.KeywordResponse;
import com.study.dto.user.UserResponse;
import com.study.dto.user.UserUpdateDistanceRequest;
import com.study.dto.user.UserUpdateNickNameRequest;
import com.study.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

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
                                                      @RequestBody @Valid UserUpdateNickNameRequest request) {
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

    @PatchMapping("/users/areas/{areaId}")
    public ResponseEntity<UserResponse> updateArea(@LoginUser Long userId, Long areaId) {
        return ResponseEntity.ok(userService.updateArea(userId, areaId));
    }

    @PatchMapping("/users/distance")
    public ResponseEntity<UserResponse> updateDistance(@LoginUser Long userId,
                                                             @RequestBody @Valid UserUpdateDistanceRequest request) {
        return ResponseEntity.ok(userService.updateDistance(userId, request));
    }

    @PostMapping("/users/keywords")
    public ResponseEntity<Void> addKeyword(@LoginUser Long userId,
                                           @RequestBody @Valid KeywordCreateRequest request) {
        userService.addKeyword(userId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/users/keywords/{keywordId}")
    public ResponseEntity<Void> deleteKeyword(@LoginUser Long userId, @PathVariable Long keywordId) {
        userService.deleteKeyword(userId, keywordId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users/keywords")
    public ResponseEntity<List<KeywordResponse>> findKeywordById(@LoginUser Long userId){
        return ResponseEntity.ok(userService.findKeywordById(userId));
    }

}
