package com.study.studyservice.controller;

import com.study.studyservice.config.LoginUser;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@Slf4j
@CrossOrigin(origins = "*",allowedHeaders = "*")
@RestController
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/studies")
    public ResponseEntity<StudyResponse> create(@LoginUser Long userId,
                                                @RequestPart(required = false) MultipartFile image,
                                                @RequestPart @Valid StudyCreateRequest request){
        return ResponseEntity.ok(studyService.create(userId,image,request));
    }
}
