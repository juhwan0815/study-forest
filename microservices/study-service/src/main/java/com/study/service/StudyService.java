package com.study.service;

import com.study.client.UserResponse;
import com.study.dto.study.*;
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
}
