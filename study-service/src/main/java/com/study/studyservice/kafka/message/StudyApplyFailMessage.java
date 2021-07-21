package com.study.studyservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyApplyFailMessage {

    private Long userId;

    private Long studyId;

    private String studyName;

    public static StudyApplyFailMessage from(Long userId,Long studyId,String studyName){
        StudyApplyFailMessage studyApplyFailMessage = new StudyApplyFailMessage();
        studyApplyFailMessage.userId = userId;
        studyApplyFailMessage.studyName = studyName;
        studyApplyFailMessage.studyId = studyId;
        return studyApplyFailMessage;
    }
}
