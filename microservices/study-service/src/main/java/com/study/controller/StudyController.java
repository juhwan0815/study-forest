package com.study.controller;

import com.study.client.UserResponse;
import com.study.config.LoginUser;
import com.study.dto.chatroom.ChatRoomCreateRequest;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.chatroom.ChatRoomUpdateRequest;
import com.study.dto.study.*;
import com.study.dto.studyuser.StudyUserResponse;
import com.study.dto.tag.TagCreateRequest;
import com.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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

    @PatchMapping("/studies/{studyId}/areas")
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
    public ResponseEntity<Page<StudyResponse>> search(@LoginUser Long userId,
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
    public ResponseEntity<Void> failWaitUser(@LoginUser Long userId,
                                               @PathVariable Long studyId,
                                               @PathVariable Long waitUserId) {
        studyService.failWaitUser(userId, studyId, waitUserId);
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

    @DeleteMapping("/studies/{studyId}/studyUsers")
    public ResponseEntity<Void> deleteStudyUser(@LoginUser Long userId,
                                                @PathVariable Long studyId) {

        studyService.deleteStudyUser(userId, studyId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/studies/{studyId}/studyUsers")
    public ResponseEntity<List<StudyUserResponse>> findStudyUsersById(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.findStudyUsersById(studyId));
    }

    @PostMapping("/studies/{studyId}/chatRooms")
    public ResponseEntity<Void> createChatRoom(@LoginUser Long userId,
                                               @PathVariable Long studyId,
                                               @RequestBody @Valid ChatRoomCreateRequest request) {
        studyService.createChatRoom(userId, studyId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/studies/{studyId}/chatRooms/{chatRoomId}")
    public ResponseEntity<Void> updateChatRoom(@LoginUser Long userId,
                                               @PathVariable Long studyId,
                                               @PathVariable Long chatRoomId,
                                               @RequestBody @Valid ChatRoomUpdateRequest request) {
        studyService.updateChatRoom(userId, studyId, chatRoomId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/studies/{studyId}/chatRooms/{chatRoomId}")
    public ResponseEntity<Void> deleteChatRoom(@LoginUser Long userId,
                                               @PathVariable Long studyId,
                                               @PathVariable Long chatRoomId) {
        studyService.deleteChatRoom(userId, studyId, chatRoomId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/studies/{studyId}/chatRooms")
    public ResponseEntity<List<ChatRoomResponse>> findChatRoomsById(@PathVariable Long studyId) {
        return ResponseEntity.ok(studyService.findChatRoomsById(studyId));
    }

    @PostMapping("/studies/{studyId}/tags")
    public ResponseEntity<Void> addTag(@LoginUser Long userId,
                                       @PathVariable Long studyId,
                                       @RequestBody @Valid TagCreateRequest request) {
        studyService.addTag(userId, studyId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/studies/{studyId}/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(@LoginUser Long userId,
                                          @PathVariable Long studyId,
                                          @PathVariable Long tagId) {
        studyService.deleteTag(userId, studyId, tagId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/users/studies")
    public ResponseEntity<List<StudyResponse>> findByUserId(@LoginUser Long userId) {
        return ResponseEntity.ok(studyService.findByUserId(userId));
    }

    @GetMapping("/studies/waitUsers")
    public ResponseEntity<List<StudyResponse>> findByWaitUserId(@LoginUser Long userId) {
        return ResponseEntity.ok(studyService.findByWaitUserId(userId));
    }
}