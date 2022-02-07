package com.study.controller;

import com.study.client.UserResponse;
import com.study.config.LoginUser;
import com.study.dto.chatroom.ChatRoomCreateRequest;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.chatroom.ChatRoomUpdateRequest;
import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudySearchRequest;
import com.study.dto.study.StudyUpdateRequest;
import com.study.dto.studyuser.StudyUserResponse;
import com.study.exception.NotExistException;
import com.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.study.exception.NotExistException.IMAGE_NOT_EXIST;

@RestController
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;

    @PostMapping("/studies/imageUrls")
    public ResponseEntity<Map<String, String>> convertToImageUrl(@RequestPart MultipartFile image) {

        if (image.isEmpty()) {
            throw new NotExistException(IMAGE_NOT_EXIST);
        }

        Map<String, String> response = new HashMap<>();
        String imageUrl = studyService.uploadImage(image);
        response.put("imageUrl", imageUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/studies")
    public ResponseEntity<Map<String, Long>> create(@LoginUser Long userId,
                                                    @RequestBody @Valid StudyCreateRequest request) {
        Long studyId = studyService.create(userId, request);
        Map<String, Long> response = new HashMap<>();
        response.put("studyId", studyId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/studies/{studyId}")
    public ResponseEntity<Void> update(@LoginUser Long userId,
                                       @PathVariable Long studyId,
                                       @RequestBody @Valid StudyUpdateRequest request) {
        studyService.update(userId, studyId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
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
    public ResponseEntity<List<StudyResponse>> search(@LoginUser Long userId,
                                                      @Valid StudySearchRequest request) {
        return ResponseEntity.ok(studyService.search(userId, request));
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

    // 여기 부터
    @GetMapping("/users/studies")
    public ResponseEntity<List<StudyResponse>> findByUserId(@LoginUser Long userId) {
        return ResponseEntity.ok(studyService.findByUserId(userId));
    }

    @GetMapping("/studies/waitUsers")
    public ResponseEntity<List<StudyResponse>> findByWaitUserId(@LoginUser Long userId) {
        return ResponseEntity.ok(studyService.findByWaitUserId(userId));
    }

    @GetMapping("/studies/chatRooms/{chatRoomId}")
    public ResponseEntity<StudyResponse> findByChatRoomId(@PathVariable Long chatRoomId) {
        return ResponseEntity.ok(studyService.findByChatRoomId(chatRoomId));
    }

    @GetMapping("/studies/{studyId}/{chatRooms}/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse> findChatRoomByIdAndChatRoomId(@PathVariable Long studyId,
                                                                          @PathVariable Long chatRoomId) {
        return ResponseEntity.ok(studyService.findChatRoomByIdAndChatRoomId(studyId, chatRoomId));
    }
}
