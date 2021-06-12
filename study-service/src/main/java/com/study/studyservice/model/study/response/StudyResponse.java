package com.study.studyservice.model.study.response;

import com.study.studyservice.domain.Study;
import com.study.studyservice.domain.StudyStatus;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.model.location.response.LocationResponse;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class StudyResponse {

    private Long id;

    private String name; // 이름

    private int numberOfPeople; // 참여인원

    private int currentNumberOfPeople; // 현재 참여 인원

    private String content; // 내용

    private boolean online; // 온라인 여부

    private boolean offline; // 오프라인 여부

    private StudyStatus status; // 스터디 상태

    private String studyImage; // 이미지 URL

    private String studyThumbnailImage; // 썸네일 이미지 URL

    private LocationResponse location; // 지역 정보

    private CategoryResponse parentCategory; // 부모 카테고리

    private CategoryResponse childCategory; // 자식 카테고리

    private List<String> studyTags; // 스터디 태그

    public static StudyResponse from(Study study,LocationResponse location) {
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.id = study.getId();
        studyResponse.name = study.getName();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.status = study.getStatus();
        studyResponse.content = study.getContent();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.studyImage = study.getStudyImage();
        studyResponse.studyThumbnailImage = study.getStudyThumbnailImage();
        studyResponse.location = location;
        studyResponse.parentCategory = CategoryResponse.from(study.getCategory().getParent());
        studyResponse.childCategory = CategoryResponse.from(study.getCategory());
        studyResponse.studyTags = study.getStudyTags().stream()
                .map(studyTag -> studyTag.getTag().getName())
                .collect(Collectors.toList());
        return studyResponse;
    }


}
