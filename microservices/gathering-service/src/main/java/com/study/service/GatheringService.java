package com.study.service;

import com.study.client.UserResponse;
import com.study.domain.Gathering;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.dto.GatheringUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GatheringService {

    GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request);

    GatheringResponse update(Long userId, Long gatheringId, GatheringUpdateRequest request);

    void delete(Long userId, Long gatheringId);

    GatheringResponse findById(Long userId, Long gatheringId);

    void addGatheringUser(Long userId, Long gatheringId);

    void deleteGatheringUser(Long userId, Long gatheringId);

    List<UserResponse> findGatheringUserById(Long gatheringId);

    Page<GatheringResponse> findByStudyId(Long studyId, Pageable pageable);
}
