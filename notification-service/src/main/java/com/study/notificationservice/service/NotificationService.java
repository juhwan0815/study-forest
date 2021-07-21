package com.study.notificationservice.service;

import com.study.notificationservice.kafka.message.GatheringCreateMessage;
import com.study.notificationservice.kafka.message.StudyApplyFailMessage;
import com.study.notificationservice.kafka.message.StudyApplySuccessMessage;

public interface NotificationService {

    void gatheringCreate(GatheringCreateMessage gatheringCreateMessage);

    void studyApplyFail(StudyApplyFailMessage studyApplyFailMessage);

    void studyApplySuccess(StudyApplySuccessMessage studyApplySuccessMessage);

}
