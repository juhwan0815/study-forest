package com.study.userservice.controller;

import com.study.userservice.config.LoginUser;
import com.study.userservice.model.*;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<UserResponse> save(@RequestBody @Valid UserLoginRequest request) {
        return ResponseEntity.ok(userService.save(request));
    }

    @GetMapping("/users/{userId}/auth")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.findWithRefreshTokenById(userId));
    }

    @PatchMapping("/users/profile")
    public ResponseEntity<UserResponse> changeProfile(@LoginUser Long userId,
                                                      @RequestPart(required = false) MultipartFile image,
                                                      @RequestPart @Valid UserProfileUpdateRequest request){
        return ResponseEntity.ok(userService.profileUpdate(userId,image,request));
    }


}
