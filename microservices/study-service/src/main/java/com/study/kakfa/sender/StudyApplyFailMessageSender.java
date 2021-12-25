package com.study.kakfa.sender;


import com.study.kakfa.StudyApplyFailMessage;

public interface StudyApplyFailMessageSender {

    void send(StudyApplyFailMessage studyApplyFailMessage);
}
