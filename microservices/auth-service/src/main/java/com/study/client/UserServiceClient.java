package com.study.client;

import com.study.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users/{kakaoId}")
    UserResponse findUserByKakaoId(@PathVariable Long kakaoId);


}
