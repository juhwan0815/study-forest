package com.study.studyservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyApplySuccessMessage {

    private Long userId;

    private Long studyId;

    public static StudyApplySuccessMessage from(Long userId, Long studyId){
        StudyApplySuccessMessage studyApplySuccessMessage = new StudyApplySuccessMessage();
        studyApplySuccessMessage.userId = userId;
        studyApplySuccessMessage.studyId = studyId;
        return studyApplySuccessMessage;
    }
}
