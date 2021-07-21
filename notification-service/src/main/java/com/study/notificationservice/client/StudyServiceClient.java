package com.study.notificationservice.client;

import com.study.notificationservice.model.study.StudyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "study-service")
public interface StudyServiceClient {

    @GetMapping("/studies/{studyId}/studyUsers")
    StudyResponse findWithStudyUserByStudyId(@PathVariable Long studyId);
}

