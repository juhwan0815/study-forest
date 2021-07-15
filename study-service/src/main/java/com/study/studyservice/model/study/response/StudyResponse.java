package com.study.studyservice.model.study.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.studyservice.domain.*;
import com.study.studyservice.model.category.response.CategoryResponse;
import com.study.studyservice.model.location.response.LocationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudyResponse {

    private Long id;

    private String name; // 이름

    private int numberOfPeople; // 참여인원

    private int currentNumberOfPeople; // 현재 참여 인원

    private String content; // 내용

    private boolean online; // 온라인 여부

    private boolean offline; // 오프라인 여부

    private StudyStatus status; // 스터디 상태

    private Image image; // 스터디 이미지

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Boolean apply; // 신청 여부

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private LocationResponse location; // 지역 정보

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private CategoryResponse parentCategory; // 부모 카테고리

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private CategoryResponse childCategory; // 자식 카테고리

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> studyTags; // 스터디 태그

    public static StudyResponse from(Study study,LocationResponse location,Long userId) {
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.id = study.getId();
        studyResponse.name = study.getName();
        studyResponse.content = study.getContent();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.status = study.getStatus();
        studyResponse.image = study.getImage();
        studyResponse.location = location;
        studyResponse.parentCategory = CategoryResponse.from(study.getCategory().getParent());
        studyResponse.childCategory = CategoryResponse.from(study.getCategory());
        studyResponse.studyTags = study.getStudyTags().stream()
                .map(studyTag -> studyTag.getTag().getName())
                .collect(Collectors.toList());

        if(userId == null){
            studyResponse.apply = false;
            return studyResponse;
        }

        // 스터디 대기 인원에 있으면 true 없으면 false
        Optional<WaitUser> optionalWaitUser = study.getWaitUsers().stream()
                .filter(waitUser -> waitUser.getUserId().equals(userId))
                .findFirst();

        if (optionalWaitUser.isPresent()){
            studyResponse.apply = true;
            return studyResponse;
        } else {
            studyResponse.apply = false;
        }

        // 스터디 참가 인원에 있으면 null
        Optional<StudyUser> findStudyUser = study.getStudyUsers().stream()
                .filter(studyUser -> studyUser.getUserId().equals(userId))
                .findAny();

        if (findStudyUser.isPresent()){
            studyResponse.apply = null;
        }

        return studyResponse;
    }

    public static StudyResponse from(Study study,LocationResponse location) {
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.id = study.getId();
        studyResponse.name = study.getName();
        studyResponse.content = study.getContent();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.status = study.getStatus();
        studyResponse.image = study.getImage();
        studyResponse.location = location;
        studyResponse.parentCategory = CategoryResponse.from(study.getCategory().getParent());
        studyResponse.childCategory = CategoryResponse.from(study.getCategory());
        studyResponse.studyTags = study.getStudyTags().stream()
                .map(studyTag -> studyTag.getTag().getName())
                .collect(Collectors.toList());

        return studyResponse;
    }

    public static StudyResponse from(Study study){
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.id = study.getId();
        studyResponse.name = study.getName();
        studyResponse.content = study.getContent();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.status = study.getStatus();
        studyResponse.image = study.getImage();
        return studyResponse;
    }

    public static StudyResponse searchFrom(Study study){
        StudyResponse studyResponse = new StudyResponse();
        studyResponse.id = study.getId();
        studyResponse.name = study.getName();
        studyResponse.content = study.getContent();
        studyResponse.numberOfPeople = study.getNumberOfPeople();
        studyResponse.currentNumberOfPeople = study.getCurrentNumberOfPeople();
        studyResponse.online = study.isOnline();
        studyResponse.offline = study.isOffline();
        studyResponse.status = study.getStatus();
        studyResponse.image = study.getImage();
        studyResponse.studyTags = study.getStudyTags().stream()
                .map(studyTag -> studyTag.getTag().getName())
                .collect(Collectors.toList());
        return studyResponse;
    }

}
