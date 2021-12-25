package com.study.controller;

import com.study.config.LoginUser;
import com.study.dto.study.*;
import com.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<StudyResponse> findById(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.findById(studyId));
    }

    @GetMapping("/studies")
    public ResponseEntity<Slice<StudyResponse>> search(@LoginUser Long userId,
                                                      @PageableDefault(size = 25,page = 0) Pageable pageable,
                                                      @Valid StudySearchRequest request){
        return ResponseEntity.ok(studyService.search(userId, pageable, request));
    }


}
