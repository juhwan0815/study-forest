package com.study.userservice.controller;

import com.study.userservice.config.LoginUser;
import com.study.userservice.model.UserImageUpdateRequest;
import com.study.userservice.model.UserLoginRequest;
import com.study.userservice.model.UserNickNameUpdateRequest;
import com.study.userservice.model.UserResponse;
import com.study.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/users/image")
    public ResponseEntity<UserResponse> changeProfile(@LoginUser Long userId,
                                                      @Valid UserImageUpdateRequest request) {
        return ResponseEntity.ok(userService.imageUpdate(userId,request));
    }

    @PatchMapping("/users/nickname")
    public ResponseEntity<UserResponse> changeNickName(@LoginUser Long userId,
                                                       @RequestBody @Valid UserNickNameUpdateRequest request){
        return ResponseEntity.ok(userService.nickNameUpdate(userId,request));
    }




}
