package com.study.notificationservice.service;

import com.study.notificationservice.kafka.message.*;

public interface NotificationService {

    void gatheringCreate(GatheringCreateMessage gatheringCreateMessage);

    void studyApplyFail(StudyApplyFailMessage studyApplyFailMessage);

    void studyApplySuccess(StudyApplySuccessMessage studyApplySuccessMessage);

    void studyCreate(StudyCreateMessage studyCreateMessage);

    void chatCreate(ChatCreateMessage chatCreateMessage);

}
