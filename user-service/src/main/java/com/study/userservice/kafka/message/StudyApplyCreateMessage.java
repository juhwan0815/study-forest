package com.study.userservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
