package com.study.study.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyCreateRequest {

    @NotBlank(message = "스터디 이름은 필수입니다.")
    private String name;

    @Min(value = 2, message = "참여인원은 최소 2명이어야 합니다.")
    @NotNull(message = "참여인원은 필수입니다.")
    private Integer numberOfPeople;

    @NotBlank(message = "스터디 설명은 필수입니다.")
    private String content;

    @NotNull(message = "태그는 필수입니다.")
    private List<String> tags;

    @NotNull(message = "온라인 여부는 필수입니다.")
    private Boolean online;

    @NotNull(message = "오프라인 여부는 필수입니다.")
    private Boolean offline;

    private String code;

    @NotNull(message = "카테고리 ID 는 필수입니다.")
    @Positive(message = "카테고리 ID 는 양수이어야 합니다.")
    private Long categoryId;

    private String imageUrl;
}
