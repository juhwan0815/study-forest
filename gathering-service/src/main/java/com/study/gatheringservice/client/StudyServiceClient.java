package com.study.gatheringservice.client;

import com.study.gatheringservice.model.studyuser.StudyUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "study-service")
public interface StudyServiceClient {

    @GetMapping("/studies/{studyId}/users")
    List<StudyUserResponse> findStudyUserByStudyId(@PathVariable Long studyId);
}
