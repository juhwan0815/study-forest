package com.study.controller;

import com.study.client.UserResponse;
import com.study.config.LoginUser;
import com.study.dto.chatroom.ChatRoomCreateRequest;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.study.*;
import com.study.dto.studyuser.StudyUserResponse;
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
import java.util.List;

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
                                                       @PageableDefault(size = 25, page = 0) Pageable pageable,
                                                       @Valid StudySearchRequest request) {
        return ResponseEntity.ok(studyService.search(userId, pageable, request));
    }

    @PostMapping("/studies/{studyId}/waitUsers")
    public ResponseEntity<Void> createWaitUser(@LoginUser Long userId,
                                               @PathVariable Long studyId) {
        studyService.createWaitUser(userId, studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/studies/{studyId}/waitUsers")
    public ResponseEntity<Void> deleteWaitUser(@LoginUser Long userId,
                                               @PathVariable Long studyId) {
        studyService.deleteWaitUser(userId, studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/studies/{studyId}/waitUsers/{waitUserId}")
    public ResponseEntity<Void> deleteWaitUser(@LoginUser Long userId,
                                               @PathVariable Long studyId,
                                               @PathVariable Long waitUserId) {
        studyService.deleteWaitUser(userId, studyId, waitUserId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/studies/{studyId}/waitUsers")
    public ResponseEntity<List<UserResponse>> findWaitUsersById(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.findWaitUsersById(studyId));
    }

    @PostMapping("/studies/{studyId}/studyUsers/{studyUserId}")
    public ResponseEntity<Void> createStudyUser(@LoginUser Long userId,
                                                @PathVariable Long studyId,
                                                @PathVariable Long studyUserId) {
        studyService.createStudyUser(userId, studyId, studyUserId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/studies/{studyId}/studyUsers/{studyUserId}")
    public ResponseEntity<Void> deleteStudyUser(@LoginUser Long userId,
                                                @PathVariable Long studyId,
                                                @PathVariable Long studyUserId) {
        studyService.deleteStudyUser(userId, studyId, studyUserId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/studeis/{studyId}/studyUsers")
    public ResponseEntity<Void> deleteStudyUser(@LoginUser Long userId,
                                                @PathVariable Long studyId) {

        studyService.deleteStudyUser(userId, studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/studies/{studyId}/studyUsers")
    public ResponseEntity<List<StudyUserResponse>> findStudyUserById(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.findStudyUsersById(studyId));
    }

    @PostMapping("/studies/{studyId}/chatRooms")
    public ResponseEntity<Void> createChatRoom(@LoginUser Long userId,
                                               @PathVariable Long studyId,
                                               @RequestBody @Valid ChatRoomCreateRequest request) {
        studyService.createChatRoom(userId, studyId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    // 회원의 참가 신청 내역 조회
    // 회원의 스터디 조회

    // 채팅방 생성

    // 채팅방 수정

    // 채팅방 삭제

    // 채팅방 조회

}
