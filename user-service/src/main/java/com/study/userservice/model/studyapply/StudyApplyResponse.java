package com.study.userservice.model.studyapply;

import com.study.userservice.domain.StudyApply;
import com.study.userservice.domain.StudyApplyStatus;
import com.study.userservice.model.study.StudyResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyApplyResponse {

    private Long id;

    private Long studyId;

    private String studyName;

    private StudyApplyStatus status;

    public static StudyApplyResponse from(StudyApply studyApply, StudyResponse study){
        StudyApplyResponse studyApplyResponse = new StudyApplyResponse();
        studyApplyResponse.id = studyApply.getId();
        studyApplyResponse.studyId = studyApply.getStudyId();
        studyApplyResponse.studyName = study.getName();
        studyApplyResponse.status = studyApply.getStatus();
        return studyApplyResponse;
    }

}
