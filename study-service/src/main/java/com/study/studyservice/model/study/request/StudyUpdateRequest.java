package com.study.studyservice.model.study.request;

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
public class StudyUpdateRequest {

    @NotBlank(message = "스터디 이름은 필수입니다.")
    private String name; // 이름

    @Min(value = 2,message = "참여인원은 최소 2명이어야 합니다.")
    @NotNull(message = "참여인원은 필수입니다.")
    private Integer numberOfPeople; // 스터디 참여 인원

    @NotBlank(message = "스터디 설명은 필수입니다.")
    private String content; // 스터디 내용

    @NotNull(message = "카테로리 ID는 필수입니다.")
    @Positive(message = "카테고리 ID는 양수이어야 합니다.")
    private Long categoryId; // 카테고리 ID

    private List<String> tags; // 스터디 태그들

    @NotNull(message = "이미지 삭제 여부는 필수입니다.")
    private Boolean deleteImage; // 이미지 삭제 여부

    @NotNull(message = "마감 여부는 필수입니다.")
    private Boolean close; // 마감 여부

    @NotNull(message = "온라인 여부는 필수입니다.")
    private Boolean online; // 온라인 여부

    @NotNull(message = "오프라인 여부는 필수입니다.")
    private Boolean offline; // 오프라인 여부

    private String locationCode; // 지역 코드

}
