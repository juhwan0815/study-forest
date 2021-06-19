package com.study.studyservice.kafka.message;

import lombok.Data;

@Data
public class StudyApplyCreateMessage {

    private Long userId;

    private Long studyId;

    public static StudyApplyCreateMessage from(Long userId, Long studyId){
        StudyApplyCreateMessage studyApplyCreateMessage = new StudyApplyCreateMessage();
        studyApplyCreateMessage.userId = userId;
        studyApplyCreateMessage.studyId = studyId;
        return studyApplyCreateMessage;
    }
}
