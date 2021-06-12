package com.study.studyservice.service;

import com.study.studyservice.model.study.request.StudyCreateRequest;
import com.study.studyservice.model.study.request.StudyUpdateRequest;
import com.study.studyservice.model.study.response.StudyResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StudyService {

    StudyResponse create(Long userId, MultipartFile image,StudyCreateRequest request);

    StudyResponse update(Long userId, Long studyId, MultipartFile image, StudyUpdateRequest request);
}
