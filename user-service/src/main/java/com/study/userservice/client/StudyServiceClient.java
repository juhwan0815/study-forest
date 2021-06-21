package com.study.userservice.client;

import com.study.userservice.model.study.StudyResponse;
import com.study.userservice.model.tag.TagResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "study-service")
public interface StudyServiceClient {

    @GetMapping("/tags/name")
    List<TagResponse> findTagsByIdIn(@RequestParam List<Long> tagIdList);

    @GetMapping("/studies/name")
    List<StudyResponse> findStudiesByIdIn(@RequestParam List<Long> studyIdList);
}
