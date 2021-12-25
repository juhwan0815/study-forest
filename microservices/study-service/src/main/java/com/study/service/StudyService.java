package com.study.service;

import com.study.dto.study.StudyCreateRequest;
import com.study.dto.study.StudyResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StudyService {

    StudyResponse create(Long userId, MultipartFile file, StudyCreateRequest request);
}
