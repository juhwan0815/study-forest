package com.study.gatheringservice.service;

import com.study.gatheringservice.model.gathering.GatheringCreateRequest;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.model.gathering.GatheringUpdateRequest;

public interface GatheringService {

    GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request);

    GatheringResponse update(Long userId, Long gatheringId, GatheringUpdateRequest request);

    void delete(Long userId, Long gatheringId);

    GatheringResponse findById(Long userId, Long gatheringId);
}
