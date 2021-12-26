package com.study.service;

import com.study.client.UserResponse;
import com.study.dto.chatroom.ChatRoomCreateRequest;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.chatroom.ChatRoomUpdateRequest;
import com.study.dto.study.*;
import com.study.dto.studyuser.StudyUserResponse;
import com.study.dto.tag.TagCreateRequest;
import com.study.kakfa.UserDeleteMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudyService {

    StudyResponse create(Long userId, MultipartFile file, StudyCreateRequest request);

    StudyResponse updateImage(Long userId, Long studyId, MultipartFile file);

    StudyResponse updateArea(Long userId, Long studyId, StudyUpdateAreaRequest request);

    StudyResponse update(Long userId, Long studyId, StudyUpdateRequest request);

    void delete(Long userId, Long studyId);

    StudyResponse findById(Long studyId);

    Slice<StudyResponse> search(Long userId, Pageable pageable, StudySearchRequest request);

    void createWaitUser(Long userId, Long studyId);

    void deleteWaitUser(Long userId, Long studyId);

    void deleteWaitUser(Long userId, Long studyId, Long waitUserId);

    List<UserResponse> findWaitUsersById(Long studyId);

    void createStudyUser(Long userId, Long studyId, Long studyUserId);

    void deleteStudyUser(Long userId, Long studyId, Long studyUserId);

    void deleteStudyUser(Long userId, Long studyId);

    List<StudyUserResponse> findStudyUsersById(Long studyId);

    void createChatRoom(Long userId, Long studyId, ChatRoomCreateRequest request);

    void updateChatRoom(Long userId, Long studyId, Long chatRoomId, ChatRoomUpdateRequest request);

    void deleteChatRoom(Long userId, Long studyId, Long chatRoomId);

    List<ChatRoomResponse> findChatRoomsById(Long studyId);

    List<StudyResponse> findByUserId(Long userId);

    List<StudyResponse> findByWaitUserId(Long userId);

    void deleteStudyUserAndWaitUser(UserDeleteMessage userDeleteMessage);

    void addTag(Long userId, Long studyId, TagCreateRequest request);
}
