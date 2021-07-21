package com.study.notificationservice.model.study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyResponse {

    private String studyName;

    private List<StudyUserResponse> studyUsers;
}
