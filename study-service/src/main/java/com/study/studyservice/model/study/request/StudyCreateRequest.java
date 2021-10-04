package com.study.studyservice.model.study.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyCreateRequest {

    @NotBlank(message = "스터디 이름은 필수입니다.")
    private String name; // 이름

    @Min(value = 2,message = "참여인원은 최소 2명이어야 합니다.")
    @NotNull(message = "참여인원은 필수입니다.")
    private int numberOfPeople; // 참여인원

    @NotBlank(message = "스터디 설명은 필수입니다.")
    private String content; // 내용

    private List<String> tags; // 해쉬태그들

    @NotNull(message = "온라인 여부는 필수입니다.")
    private Boolean online; // 온라인 여부

    @NotNull(message = "오프라인 여부는 필수입니다.")
    private Boolean offline; // 오프라인 여부

    private String locationCode; // 지역정보

    @NotNull(message = "카테고리 ID는 필수입니다.")
    @Positive(message = "카테고리 ID는 양수이어야 합니다.")
    private Long categoryId; // 카테고리 ID
}
