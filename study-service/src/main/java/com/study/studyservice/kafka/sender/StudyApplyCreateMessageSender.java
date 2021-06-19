package com.study.studyservice.kafka.sender;

import com.study.studyservice.kafka.message.StudyApplyCreateMessage;

public interface StudyApplyCreateMessageSender {

    void send(StudyApplyCreateMessage studyApplyCreateMessage);
}
