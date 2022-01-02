package com.study.dto.study;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

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
}
