package com.study.studyservice.client;

import com.study.studyservice.model.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users")
    List<UserResponse> findUserByIdIn(@RequestParam List<Long> userIdList);

    @GetMapping("/users/{userId}")
    UserResponse findUserById(@PathVariable Long userId);

}
