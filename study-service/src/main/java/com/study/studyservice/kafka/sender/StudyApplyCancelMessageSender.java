package com.study.studyservice.kafka.sender;

import com.study.studyservice.kafka.message.StudyApplyCancelMessage;

public interface StudyApplyCancelMessageSender {

    void send(StudyApplyCancelMessage studyApplyCancelMessage);
}
