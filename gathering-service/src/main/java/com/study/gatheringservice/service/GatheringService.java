package com.study.gatheringservice.service;

import com.study.gatheringservice.kafka.message.StudyDeleteMessage;
import com.study.gatheringservice.model.gathering.GatheringCreateRequest;
import com.study.gatheringservice.model.gathering.GatheringResponse;
import com.study.gatheringservice.model.gathering.GatheringUpdateRequest;
import com.study.gatheringservice.model.gatheringuser.GatheringUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GatheringService {

    GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request);

    GatheringResponse update(Long userId, Long gatheringId, GatheringUpdateRequest request);

    void delete(Long userId, Long gatheringId);

    GatheringResponse findById(Long userId, Long gatheringId);

    Page<GatheringResponse> find(Long studyId, Pageable pageable);

    void addGatheringUser(Long userId, Long gatheringId);

    void deleteGatheringUser(Long userId, Long gatheringId);

    List<GatheringUserResponse> findGatheringUsers(Long gatheringId);

    void deleteByStudyId(StudyDeleteMessage studyDeleteMessage);

}