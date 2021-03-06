package com.study.service;

import com.study.client.UserResponse;
import com.study.domain.Gathering;
import com.study.dto.GatheringCreateRequest;
import com.study.dto.GatheringResponse;
import com.study.dto.GatheringUpdateRequest;
import com.study.kakfa.StudyDeleteMessage;
import com.study.kakfa.UserDeleteMessage;
import com.study.kakfa.listener.UserDeleteListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface GatheringService {

    GatheringResponse create(Long userId, Long studyId, GatheringCreateRequest request);

    GatheringResponse update(Long userId, Long gatheringId, GatheringUpdateRequest request);

    void delete(Long userId, Long gatheringId);

    GatheringResponse findById(Long gatheringId);

    void addGatheringUser(Long userId, Long gatheringId);

    void deleteGatheringUser(Long userId, Long gatheringId);

    void deleteGatheringUser(UserDeleteMessage userDeleteMessage);

    List<UserResponse> findGatheringUserById(Long gatheringId);

    Slice<GatheringResponse> findByStudyId(Long studyId, Pageable pageable);

    void deleteByStudyId(StudyDeleteMessage studyDeleteMessage);
}
