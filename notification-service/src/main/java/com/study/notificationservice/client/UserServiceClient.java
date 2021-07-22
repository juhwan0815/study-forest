package com.study.notificationservice.client;

import com.study.notificationservice.model.user.UserResponse;
import com.study.notificationservice.model.user.UserWithTagResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserResponse findUserById(@PathVariable Long userId);

    @GetMapping("/users/interestTags")
    List<UserWithTagResponse> findWithInterestTagsByTagIdList(@RequestParam List<Long> tagIdList);
}