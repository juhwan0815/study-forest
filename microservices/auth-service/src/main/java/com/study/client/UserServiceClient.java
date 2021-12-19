package com.study.client;

import com.study.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @PostMapping("/users/{kakaoId}")
    UserResponse loginByKakaoId(@PathVariable Long kakaoId, @RequestHeader String fcmToken);


}
