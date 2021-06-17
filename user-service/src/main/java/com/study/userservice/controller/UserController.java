package com.study.userservice.controller;

import com.study.userservice.config.LoginUser;
import com.study.userservice.model.user.UserLoginRequest;
import com.study.userservice.model.user.UserUpdateProfileRequest;
import com.study.userservice.model.user.UserResponse;
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
        return ResponseEntity.ok(userService.create(request));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long userId){
        return ResponseEntity.ok(userService.findById(userId));
    }

    @PatchMapping("/users/profile")
    public ResponseEntity<UserResponse> changeProfile(@LoginUser Long userId,
                                                      @RequestPart(required = false) MultipartFile image,
                                                      @RequestPart @Valid UserUpdateProfileRequest request){
        return ResponseEntity.ok(userService.updateProfile(userId,image,request));
    }


}
