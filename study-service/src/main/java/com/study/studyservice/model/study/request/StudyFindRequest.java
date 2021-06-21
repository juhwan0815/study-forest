package com.study.studyservice.model.study.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyFindRequest {

    private List<Long> studyIdList;

}
