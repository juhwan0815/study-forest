package com.study.service;

import com.study.dto.study.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

public interface StudyService {

    StudyResponse create(Long userId, MultipartFile file, StudyCreateRequest request);

    StudyResponse updateImage(Long userId, Long studyId, MultipartFile file);

    StudyResponse updateArea(Long userId, Long studyId, StudyUpdateAreaRequest request);

    StudyResponse update(Long userId, Long studyId, StudyUpdateRequest request);

    void delete(Long userId, Long studyId);

    StudyResponse findById(Long studyId);

    Slice<StudyResponse> search(Long userId, Pageable pageable, StudySearchRequest request);
}
