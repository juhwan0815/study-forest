package com.study.studyservice.model.study.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyUpdateRequest {

    @NotBlank
    private String name; // 이름

    private Integer numberOfPeople; // 스터디 참여 인원

    private String content; // 스터디 내용

    private Long categoryId; // 카테고리 ID

    private List<String> tags; // 스터디 태그들

    private boolean deleteImage; // 이미지 삭제 여부

    private boolean close; // 마감 여부

    private boolean online; // 온라인 여부

    private boolean offline; // 오프라인 여부

    private String locationCode; // 지역 코드

}
