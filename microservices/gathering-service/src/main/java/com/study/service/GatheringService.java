package com.study.service;

import com.study.domain.Gathering;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.dto.GatheringUpdateRequest;

public interface GatheringService {

    GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request);

    GatheringResponse update(Long userId, Long gatheringId, GatheringUpdateRequest request);
}
