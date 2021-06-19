package com.study.studyservice.kafka.sender;

import com.study.studyservice.kafka.message.StudyApplyFailMessage;

public interface StudyApplyFailMessageSender {

    void send(StudyApplyFailMessage studyApplyFailMessage);
}
