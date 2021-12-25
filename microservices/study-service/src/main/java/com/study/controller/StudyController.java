package com.study.controller;

import com.study.config.LoginUser;
import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudyUpdateAreaRequest;
import com.study.dto.study.StudyUpdateRequest;
import com.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.ws.rs.Path;

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

    @PatchMapping("/studies/{studyId}")
    public ResponseEntity<StudyResponse> update(@LoginUser Long userId,
                                                @PathVariable Long studyId,
                                                @RequestBody @Valid StudyUpdateRequest request) {
        return ResponseEntity.ok(studyService.update(userId, studyId, request));
    }

    @PatchMapping("/studies/{studyId}/images")
    public ResponseEntity<StudyResponse> updateImage(@LoginUser Long userId,
                                                     @PathVariable Long studyId,
                                                     @RequestPart(required = false) MultipartFile file) {
        return ResponseEntity.ok(studyService.updateImage(userId, studyId, file));
    }

    @PatchMapping("/studies/{studyId}/area")
    public ResponseEntity<StudyResponse> updateArea(@LoginUser Long userId,
                                                    @PathVariable Long studyId,
                                                    @RequestBody @Valid StudyUpdateAreaRequest request) {
        return ResponseEntity.ok(studyService.updateArea(userId, studyId, request));
    }

    @DeleteMapping("/studies/{studyId}")
    public ResponseEntity<Void> delete(@LoginUser Long userId,
                                       @PathVariable Long studyId) {
        studyService.delete(userId, studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/studies/{studyId}")
    public ResponseEntity<StudyResponse> findById(@LoginUser Long userId, @PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.findById(userId, studyId));
    }




}
