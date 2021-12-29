package com.study.client;

import org.apache.catalina.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "study-service")
public interface StudyServiceClient {

    @GetMapping("/studies/{studyId}")
    StudyResponse findById(@PathVariable Long studyId);

    @GetMapping("/studies/{studyId}/studyUsers")
    List<UserResponse> findStudyUsersById(@PathVariable Long studyId);
}

