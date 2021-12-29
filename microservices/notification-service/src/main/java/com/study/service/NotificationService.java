package com.study.service;

import com.study.dto.NotificationResponse;
import com.study.kakfa.GatheringCreateMessage;
import com.study.kakfa.StudyApplyFailMessage;
import com.study.kakfa.StudyApplySuccessMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationService {

    Slice<NotificationResponse> findByUserId(Long userId, Pageable pageable);

    void studyApplyFail(StudyApplyFailMessage studyApplyFailMessage);

    void studyApplySuccess(StudyApplySuccessMessage studyApplySuccessMessage);

    void gatheringCreate(GatheringCreateMessage gatheringCreateMessage);
}
