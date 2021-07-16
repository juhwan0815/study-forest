package com.study.gatheringservice.model.studyuser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyUserResponse {

    private Long id;

    private Long userId;

    private String nickName;

    private Role role;
}
