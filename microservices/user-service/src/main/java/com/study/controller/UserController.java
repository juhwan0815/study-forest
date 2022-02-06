package com.study.controller;

import com.study.config.LoginUser;
import com.study.dto.keyword.KeywordCreateRequest;
import com.study.dto.keyword.KeywordResponse;
import com.study.dto.user.UserFindRequest;
import com.study.dto.user.UserResponse;
import com.study.dto.user.UserUpdateDistanceRequest;
import com.study.dto.user.UserUpdateRequest;
import com.study.exception.NotExistException;
import com.study.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.study.exception.NotExistException.IMAGE_NOT_EXIST;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<Void> create(@RequestHeader String kakaoToken) {
        userService.create(kakaoToken);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> findByIdIn(@Valid UserFindRequest request) {
        return ResponseEntity.ok(userService.findByIdIn(request));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PostMapping("/users/{kakaoId}")
    public ResponseEntity<UserResponse> login(@PathVariable Long kakaoId, @RequestHeader String fcmToken) {
        return ResponseEntity.ok(userService.findByKakaoId(kakaoId, fcmToken));
    }

    @PostMapping("/api/users/imageUrls")
    public ResponseEntity<Map<String, String>> convertToImageUrl(@RequestPart MultipartFile image) {

        if (image.isEmpty()) {
            throw new NotExistException(IMAGE_NOT_EXIST);
        }

        Map<String, String> response = new HashMap<>();
        String imageUrl = userService.uploadImage(image);
        response.put("imageUrl", imageUrl);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/users/profile")
    public ResponseEntity<Void> update(@LoginUser Long userId,
                                       @RequestBody @Valid UserUpdateRequest request) {
        userService.update(userId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users/profile")
    public ResponseEntity<UserResponse> findByLoginId(@LoginUser Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PatchMapping("/users/areas/{areaId}")
    public ResponseEntity<Void> updateArea(@LoginUser Long userId, @PathVariable Long areaId) {
        userService.updateArea(userId, areaId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/users/distance")
    public ResponseEntity<Void> updateDistance(@LoginUser Long userId,
                                               @RequestBody @Valid UserUpdateDistanceRequest request) {
        userService.updateDistance(userId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
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
    public ResponseEntity<List<KeywordResponse>> findKeywordById(@LoginUser Long userId) {
        return ResponseEntity.ok(userService.findKeywordById(userId));
    }

    @GetMapping("/users/keywords/notifications")
    public ResponseEntity<List<UserResponse>> findByKeywordContentContain(@RequestParam String content) {
        return ResponseEntity.ok(userService.findByKeywordContentContain(content));
    }

}
