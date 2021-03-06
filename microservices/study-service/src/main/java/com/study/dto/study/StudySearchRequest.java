package com.study.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudySearchRequest {

    private String keyword;

    private Long categoryId;

    @NotNull(message = "온라인 여부는 필수입니다.")
    private Boolean online;

    @NotNull(message = "오프라인 여부는 필수입니다.")
    private Boolean offline;

    @NotNull(message = "사이즈는 필수입니다.")
    @Positive(message = "사이즈는 양수이어야 합니다.")
    private Integer size;

    private Long studyId;
}
