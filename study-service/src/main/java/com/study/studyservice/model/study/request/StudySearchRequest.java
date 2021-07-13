package com.study.studyservice.model.study.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudySearchRequest {

    private String searchKeyword;

    private Long categoryId;

    @NotNull
    private Boolean online;

    @NotNull
    private Boolean offline;

}
