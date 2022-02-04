package com.study.study.controller;

import com.study.config.LoginUser;
import com.study.study.dto.StudyCreateRequest;
import com.study.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/api/studies")
    public ResponseEntity<Map<String, Long>> create(@LoginUser Long userId,
                                                    @RequestBody @Valid StudyCreateRequest request) {
        Long studyId = studyService.create(userId, request);
        Map<String, Long> response = new HashMap<>();
        response.put("studyId", studyId);
        return ResponseEntity.ok(response);
    }
}
