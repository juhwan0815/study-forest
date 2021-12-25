package com.study.service;

import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import com.study.dto.study.StudyUpdateAreaRequest;
import com.study.dto.study.StudyUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface StudyService {

    StudyResponse create(Long userId, MultipartFile file, StudyCreateRequest request);

    StudyResponse updateImage(Long userId, Long studyId, MultipartFile file);

    StudyResponse updateArea(Long userId, Long studyId, StudyUpdateAreaRequest request);

    StudyResponse update(Long userId, Long studyId, StudyUpdateRequest request);

    void delete(Long userId, Long studyId);
}
