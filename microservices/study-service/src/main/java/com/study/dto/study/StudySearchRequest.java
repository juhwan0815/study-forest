package com.study.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudySearchRequest {

    private String keyword;

    private Long categoryId;

    private Boolean online;

    private Boolean offline;
}
