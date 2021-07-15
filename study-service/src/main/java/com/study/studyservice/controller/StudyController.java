package com.study.studyservice.controller;

import com.study.studyservice.config.LoginUser;
import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyFindRequest;
import com.study.studyservice.model.study.request.StudySearchRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.model.studyuser.StudyUserResponse;
import com.study.studyservice.model.waituser.WaitUserResponse;
import com.study.studyservice.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @GetMapping("/studies")
    public ResponseEntity<Page<StudyResponse>> search(@LoginUser Long userId,
                                                      @PageableDefault(size = 25,page = 0) Pageable pageable,
                                                      StudySearchRequest request){
        Page<StudyResponse> body = studyService.find(userId, request, pageable);
        return ResponseEntity.ok(body);
    }

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

    @GetMapping("/studies/{studyId}")
    public ResponseEntity<StudyResponse> findById(@LoginUser Long userId,@PathVariable Long studyId){
        return ResponseEntity.ok(studyService.findById(userId,studyId));
    }

    @PostMapping("/studies/{studyId}/waitUsers")
    public ResponseEntity<Void> createWaitUser(@LoginUser Long userId,
                                                 @PathVariable Long studyId){
        studyService.createWaitUser(userId,studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/studies/{studyId}/waitUsers")
    public ResponseEntity<List<WaitUserResponse>> findWaitUsersByStudyId(@PathVariable Long studyId){
        return ResponseEntity.ok(studyService.findWaitUsersByStudyId(studyId));
    }

    @PostMapping("/studies/{studyId}/users/{userId}")
    public ResponseEntity<Void> createStudyUser(@LoginUser Long loginUserId,
                                                @PathVariable Long studyId,@PathVariable Long userId){
        studyService.createStudyUser(loginUserId,studyId,userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/studies/{studyId}/waitUsers/{userId}")
    public ResponseEntity<Void> deleteWaitUser(@LoginUser Long loginUserId,
                                               @PathVariable Long studyId, @PathVariable Long userId){
        studyService.deleteWaitUser(loginUserId,studyId,userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/studies/{studyId}/users")
    public ResponseEntity<List<StudyUserResponse>> findStudyUsersByStudyId(@PathVariable Long studyId){
        return ResponseEntity.ok(studyService.findStudyUsersByStudyId(studyId));
    }


    @DeleteMapping("/studies/{studyId}/users/{userId}")
    public ResponseEntity<Void> deleteStudyUser(@LoginUser Long loginUserId,
                                                @PathVariable Long studyId, @PathVariable Long userId){
        studyService.deleteStudyUser(loginUserId,studyId,userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/studies/{studyId}/users")
    public ResponseEntity<Void> deleteStudyUserSelf(@LoginUser Long userId,@PathVariable Long studyId){
        studyService.deleteStudyUserSelf(userId,studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/studies/name")
    public ResponseEntity<List<StudyResponse>> findByIdIn(StudyFindRequest request){
        return ResponseEntity.ok(studyService.findByIdIn(request));
    }

    @GetMapping("/users/studies")
    public ResponseEntity<List<StudyResponse>> findByUser(@LoginUser Long userId){
        return ResponseEntity.ok(studyService.findByUser(userId));
    }
}
