package com.study.notificationservice.service;

import com.study.notificationservice.kafka.message.*;
import com.study.notificationservice.model.notification.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    void gatheringCreate(GatheringCreateMessage gatheringCreateMessage);

    void studyApplyFail(StudyApplyFailMessage studyApplyFailMessage);

    void studyApplySuccess(StudyApplySuccessMessage studyApplySuccessMessage);

    void studyCreate(StudyCreateMessage studyCreateMessage);

    void chatCreate(ChatCreateMessage chatCreateMessage);

    Page<NotificationResponse> findByUserId(Long userId, Pageable pageable);
}
