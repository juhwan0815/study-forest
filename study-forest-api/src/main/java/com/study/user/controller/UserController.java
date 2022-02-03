package com.study.user.controller;

import com.study.common.NotExistException;
import com.study.config.LoginUser;
import com.study.user.dto.UserResponse;
import com.study.user.dto.UserUpdateProfileRequest;
import com.study.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static com.study.common.NotExistException.IMAGE_NOT_EXIST;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/users")
    public ResponseEntity<Void> create(@RequestHeader String kakaoToken) {
        String jwtToken = userService.create(kakaoToken);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .build();
    }

    @PostMapping("/api/users/login")
    public ResponseEntity<Void> login(@RequestHeader String kakaoToken, @RequestHeader String pushToken) {
        String jwtToken = userService.login(kakaoToken, pushToken);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .build();
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

    @PatchMapping("/api/users/profile")
    public ResponseEntity<Void> updateProfile(@LoginUser Long userId,
                                              @RequestBody @Valid UserUpdateProfileRequest request) {
        userService.updateProfile(userId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/api/users/profile")
    public ResponseEntity<UserResponse> findById(@LoginUser Long userId) {
        return ResponseEntity.ok(userService.findById(userId));
    }

//    @PatchMapping("/api/users/areas/areas/{areaId}")
//    public ResponseEntity<Void> updateArea(@LoginUser Long userId, @PathVariable Long areaId)


}
