package com.study.studyservice.controller;

import com.study.studyservice.config.LoginUser;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.ws.rs.Path;

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

    @PatchMapping("/studies/{studyId}")
    public ResponseEntity<StudyResponse> update(@LoginUser Long userId,
                                                @PathVariable Long studyId,
                                                @RequestPart(required = false) MultipartFile image,
                                                @RequestPart @Valid StudyUpdateRequest request){
        return ResponseEntity.ok(studyService.update(userId,studyId,image,request));
    }

    @DeleteMapping("/studies/{studyId}")
    public ResponseEntity<Void> delete(@LoginUser Long userId,
                                       @PathVariable Long studyId){
        studyService.delete(userId,studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
