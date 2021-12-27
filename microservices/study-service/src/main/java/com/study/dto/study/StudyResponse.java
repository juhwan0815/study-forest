package com.study.dto.study;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.client.AreaResponse;
import com.study.domain.Study;
import com.study.domain.StudyStatus;
import com.study.domain.WaitStatus;
import com.study.dto.category.CategoryResponse;
import com.study.dto.tag.TagResponse;
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
    private List<TagResponse> tags; // 스터디 태그

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private WaitStatus waitStatus;

    public static StudyResponse from(Study study, AreaResponse area) {
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.studyId = study.getId();
        studyResponse.name = study.getName();
        studyResponse.content = study.getContent();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.area = area;
        studyResponse.status = study.getStatus();
        // TODO 수정
        if (study.getImage() != null) {
            studyResponse.imageUrl = study.getImage().getImageUrl();
        }
        if (study.getCategory() != null) {
            studyResponse.parentCategory = CategoryResponse.from(study.getCategory().getParent());
            studyResponse.childCategory = CategoryResponse.from(study.getCategory());
        }
        if (study.getTags().size() != 0) {
            studyResponse.tags = study.getTags().stream()
                    .map(tag -> TagResponse.from(tag))
                    .collect(Collectors.toList());
        }
        return studyResponse;
    }

    public static StudyResponse from(Study study) {
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.studyId = study.getId();
        studyResponse.name = study.getName();
        studyResponse.content = study.getContent();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.status = study.getStatus();
        if (study.getImage() != null) {
            studyResponse.imageUrl = study.getImage().getImageUrl();
        }
        return studyResponse;
    }

    public static StudyResponse fromWithTag(Study study) {
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.studyId = study.getId();
        studyResponse.name = study.getName();
        studyResponse.content = study.getContent();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.status = study.getStatus();
        if (study.getImage() != null) {
            studyResponse.imageUrl = study.getImage().getImageUrl();
        }
        studyResponse.tags = study.getTags().stream()
                .map(tag -> TagResponse.from(tag))
                .collect(Collectors.toList());
        return studyResponse;
    }

    public static StudyResponse fromWithWaitUserAndTag(Study study) {
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.studyId = study.getId();
        studyResponse.name = study.getName();
        studyResponse.content = study.getContent();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.status = study.getStatus();
        if (study.getImage() != null) {
            studyResponse.imageUrl = study.getImage().getImageUrl();
        }
        studyResponse.tags = study.getTags().stream()
                .map(tag -> TagResponse.from(tag))
                .collect(Collectors.toList());
        studyResponse.waitStatus = study.getWaitUsers().get(0).getStatus();
        return studyResponse;
    }
}
