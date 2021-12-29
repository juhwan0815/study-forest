package com.study.kakfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyApplySuccessMessage {

    private Long userId;

    private Long studyId;

    private String studyName;

    public static StudyApplySuccessMessage from(Long userId, Long studyId, String studyName){
        return new StudyApplySuccessMessage(userId, studyId, studyName);
    }
}
