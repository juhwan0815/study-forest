package com.study.study.controller;

import com.study.config.LoginUser;
import com.study.study.dto.StudyCreateRequest;
import com.study.study.dto.StudyUpdateRequest;
import com.study.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping("/api/studies/{studyId}")
    public ResponseEntity<Void> update(@LoginUser Long userId,
                                       @PathVariable Long studyId,
                                       @RequestBody @Valid StudyUpdateRequest request) {
        studyService.update(userId, studyId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }



    @PostMapping("/api/studies/imageUrls")
    public ResponseEntity<Map<String, String>> convertToImageUrl(@RequestPart MultipartFile image) {
        String imageUrl = studyService.uploadImage(image);
        Map<String, String> response = new HashMap<>();
        response.put("imageUrl", imageUrl);
        return ResponseEntity.ok(response);
    }

}
