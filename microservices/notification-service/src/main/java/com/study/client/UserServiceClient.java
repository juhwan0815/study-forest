package com.study.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserResponse findById(@PathVariable Long userId);

    @GetMapping("/users/keywords/notifications")
    List<UserResponse> findByKeywordContentContain(@RequestParam String content);
}