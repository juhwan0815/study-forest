package com.study.dto.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.querydsl.core.annotations.QueryProjection;
import com.study.client.AreaResponse;
import com.study.domain.Study;
import com.study.domain.StudyStatus;
import com.study.dto.category.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyResponse {

    private Long studyId;

    private String name; // 이름

    private int numberOfPeople; // 참여인원

    private int currentNumberOfPeople; // 현재 참여 인원

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String content; // 내용

    private boolean online; // 온라인 여부

    private boolean offline; // 오프라인 여부

    private StudyStatus status; // 스터디 상태

    private String imageUrl;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private AreaResponse area; // 지역 정보

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private CategoryResponse parentCategory; // 부모 카테고리

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private CategoryResponse childCategory; // 자식 카테고리

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> tags; // 스터디 태그

    public static StudyResponse from(Study study, AreaResponse area) {
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.studyId = study.getId();
        studyResponse.name = study.getName();
        studyResponse.content = study.getContent();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.imageUrl = study.getImageUrl();
        studyResponse.area = area;
        studyResponse.status = study.getStatus();
        studyResponse.childCategory = CategoryResponse.from(study.getCategory());
        studyResponse.parentCategory = CategoryResponse.from(study.getCategory().getParent());
        studyResponse.tags = study.getTags().stream()
                .map(tag -> tag.getContent())
                .collect(Collectors.toList());
        return studyResponse;
    }

    public static StudyResponse from(Study study) {
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.studyId = study.getId();
        studyResponse.name = study.getName();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.imageUrl = study.getImageUrl();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.status = study.getStatus();
        return studyResponse;
    }

    @QueryProjection
    public StudyResponse(Study study) {
        this.studyId = study.getId();
        this.name = study.getName();
        this.numberOfPeople = study.getNumberOfPeople();
        this.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        this.online = study.isOnline();
        this.offline = study.isOffline();
        this.status = study.getStatus();
        this.imageUrl = study.getImageUrl();
        this.tags = study.getTags().stream()
                .map(tag -> tag.getContent())
                .collect(Collectors.toList());
    }

}
