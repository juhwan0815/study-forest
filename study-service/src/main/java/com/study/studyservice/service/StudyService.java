package com.study.studyservice.service;

import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyFindRequest;
import com.study.studyservice.model.study.request.StudySearchRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import com.study.studyservice.model.studyuser.StudyUserResponse;
import com.study.studyservice.model.waituser.WaitUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudyService {

    StudyResponse create(Long userId, MultipartFile image,StudyCreateRequest request);

    StudyResponse update(Long userId, Long studyId, MultipartFile image, StudyUpdateRequest request);

    Page<StudyResponse> find(Long userId, StudySearchRequest request, Pageable pageable);

    void delete(Long userId, Long studyId);

    StudyResponse findById(Long studyId);

    void createWaitUser(Long userId, Long studyId);

    List<WaitUserResponse> findWaitUsersByStudyId(Long studyId);

    void createStudyUser(Long loginUserId, Long studyId, Long userId);

    void deleteWaitUser(Long loginUserId, Long studyId, Long userId);

    List<StudyUserResponse> findStudyUsersByStudyId(Long studyId);

    void deleteStudyUser(Long loginUserId, Long studyId, Long userId);

    void deleteStudyUserSelf(Long userId, Long studyId);

    List<StudyResponse> findByIdIn(StudyFindRequest request);

    List<StudyResponse> findByUser(Long userId);

}
