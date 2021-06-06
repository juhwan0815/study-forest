package com.study.authservice.client;


import com.study.authservice.model.UserLoginRequest;
import com.study.authservice.model.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/users")
    UserResponse login(@RequestBody UserLoginRequest request);

    @GetMapping("/users/{userId}/auth")
    UserResponse findUserById(@PathVariable Long userId);
}
