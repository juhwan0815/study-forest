package com.study.authservice.client;


import com.study.authservice.model.UserLoginRequest;
import com.study.authservice.model.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/login")
    UserResponse login(@RequestBody UserLoginRequest request);
}
