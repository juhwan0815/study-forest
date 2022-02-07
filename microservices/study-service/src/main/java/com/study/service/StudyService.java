package com.study.service;

import com.study.client.UserResponse;
import com.study.dto.chatroom.ChatRoomCreateRequest;
import com.study.dto.chatroom.ChatRoomResponse;
import com.study.dto.chatroom.ChatRoomUpdateRequest;
import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudySearchRequest;
import com.study.dto.study.StudyUpdateRequest;
import com.study.dto.studyuser.StudyUserResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudyService {

    Long create(Long userId, StudyCreateRequest request);

    void update(Long userId, Long studyId, StudyUpdateRequest request);

    void delete(Long userId, Long studyId);

    StudyResponse findById(Long studyId);

    List<StudyResponse> search(Long userId, StudySearchRequest request);

    void createWaitUser(Long userId, Long studyId);

    void deleteWaitUser(Long userId, Long studyId);

    void failWaitUser(Long userId, Long studyId, Long waitUserId);

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

    StudyResponse findByChatRoomId(Long chatRoomId);

    ChatRoomResponse findChatRoomByIdAndChatRoomId(Long studyId, Long chatRoomId);

    String uploadImage(MultipartFile image);
}
