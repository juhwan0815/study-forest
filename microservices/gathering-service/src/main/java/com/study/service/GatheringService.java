package com.study.service;

import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;

public interface GatheringService {

    GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request);
}
