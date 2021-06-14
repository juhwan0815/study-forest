package com.study.authservice.client;


import com.study.authservice.model.user.UserLoginRequest;
import com.study.authservice.model.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/users")
    UserResponse login(@RequestBody UserLoginRequest request);

    @GetMapping("/users/{userId}")
    UserResponse findUserById(@PathVariable Long userId);
}
