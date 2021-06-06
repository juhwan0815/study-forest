package com.study.userservice.controller;

import com.study.userservice.model.UserLoginRequest;
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
    public ResponseEntity<UserResponse> save(@RequestBody @Valid UserLoginRequest request){
        return ResponseEntity.ok(userService.save(request));
    }

    @GetMapping("/users/{userId}/auth")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long userId){
        return ResponseEntity.ok(userService.findWithRefreshTokenById(userId));
    }


}
