package com.study.service;

import com.study.dto.NotificationResponse;
import com.study.kakfa.StudyApplyFailMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationService {

    Slice<NotificationResponse> findByUserId(Long userId, Pageable pageable);

    void studyApplyFail(StudyApplyFailMessage studyApplyFailMessage);

}
