package com.study.gatheringservice.client;

import com.study.gatheringservice.model.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/users")
    List<UserResponse> findUserByIdIn(@RequestParam List<Long> userIdList);

}
