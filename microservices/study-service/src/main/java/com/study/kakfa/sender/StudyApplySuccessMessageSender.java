package com.study.kakfa.sender;


import com.study.kakfa.StudyApplySuccessMessage;

public interface StudyApplySuccessMessageSender {

    void send(StudyApplySuccessMessage studyApplySuccessMessage);
}
