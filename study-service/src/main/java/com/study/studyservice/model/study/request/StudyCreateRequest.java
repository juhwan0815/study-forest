package com.study.studyservice.model.study.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyCreateRequest {

    @NotBlank
    private String name; // 이름

    private int numberOfPeople; // 참여인원

    @NotBlank
    private String content; // 내용

    private List<String> tags; // 해쉬태그들

    private boolean online; // 온라인 여부

    private boolean offline; // 오프라인 여부

    private String locationCode; // 지역정보

    private Long categoryId; // 카테고리 ID
}
