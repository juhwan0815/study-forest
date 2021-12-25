package com.study.controller;

import com.study.config.LoginUser;
import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/studies")
    public ResponseEntity<StudyResponse> create(@LoginUser Long userId,
                                                @RequestPart(required = false) MultipartFile file,
                                                @RequestPart @Valid StudyCreateRequest request) {
        return ResponseEntity.ok(studyService.create(userId, file, request));
    }

    @PatchMapping("/studies/{studyId}/images")
    public ResponseEntity<StudyResponse> update(@LoginUser Long userId,
                                                @PathVariable Long studyId,
                                                @RequestPart(required = false) MultipartFile file) {
        return ResponseEntity.ok(studyService.updateImage(userId, studyId, file));
    }
}
