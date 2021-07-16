package com.study.userservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyApplySuccessMessage {

    private Long userId;

    private Long studyId;

}