package com.study.studyservice.kafka.sender;

import com.study.studyservice.kafka.message.StudyApplySuccessMessage;

public interface StudyApplySuccessMessageSender {

    void send(StudyApplySuccessMessage studyApplySuccessMessage);
}
