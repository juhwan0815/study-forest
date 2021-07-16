package com.study.gatheringservice.service;

import com.study.gatheringservice.model.gathering.GatheringCreateRequest;
import com.study.gatheringservice.model.gathering.GatheringResponse;

public interface GatheringService {

    GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request);
}
