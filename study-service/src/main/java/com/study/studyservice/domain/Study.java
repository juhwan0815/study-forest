package com.study.studyservice.domain;

import com.study.studyservice.exception.StudyException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    private String name; // 이름

    private int numberOfPeople; // 참여인원

    private int currentNumberOfPeople; // 현재 참여 인원

    @Column(columnDefinition = "TEXT")
    private String content; // 내용

    private boolean online; // 온라인 여부

    private boolean offline; // 오프라인 여부

    @Enumerated(EnumType.STRING)
    private StudyStatus status; // 스터디 상태

    private String imageStoreName; // 이미지 저장 명

    private String studyImage; // 이미지 URL

    private String studyThumbnailImage; // 썸네일 이미지 URL

    private Long locationId; // 지역정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyTag> studyTags = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaitUser> waitUserList = new ArrayList<>();

    public static Study createStudy(String name, Integer numberOfPeople, String content, boolean online,
                                    boolean offline, String imageStoreName, String studyImage,
                                    String studyThumbnailImage, Long locationId,
                                    Category category, StudyUser user, List<StudyTag> studyTags) {
        Study study = new Study();
        study.name = name;
        study.numberOfPeople = numberOfPeople;
        study.currentNumberOfPeople = 0;
        study.content = content;
        study.online = online;
        study.offline = offline;
        study.status = StudyStatus.OPEN;
        study.studyImage = studyImage;
        study.studyThumbnailImage = studyThumbnailImage;
        study.imageStoreName = imageStoreName;
        study.locationId = locationId;
        study.category = category;
        user.setStudy(study);
        for (StudyTag studyTag : studyTags) {
            studyTag.setStudy(study);
        }
        return study;
    }

    public void plusCurrentNumberOfPeople() {
        this.currentNumberOfPeople += 1;
    }

    public void deleteImage() {
        this.studyImage = null;
        this.studyThumbnailImage = null;
        this.imageStoreName = null;
    }

    public void changeImage(String studyImage, String studyThumbnailImage, String imageStoreName) {
        this.studyImage = studyImage;
        this.studyThumbnailImage = studyThumbnailImage;
        this.imageStoreName = imageStoreName;
    }

    public void update(String name, Integer numberOfPeople, String content, boolean online, boolean offline,
                       boolean close, Long locationId, Category category, List<Tag> tags) {
        this.name = name;

        if (currentNumberOfPeople > numberOfPeople) {
            throw new StudyException("현재 스터디 인원이 수정할 인원보다 많습니다.");
        }
        this.numberOfPeople = numberOfPeople;

        this.content = content;
        this.online = online;
        this.offline = offline;
        if (close) {
            this.status = StudyStatus.CLOSE;
        }else{
            this.status = StudyStatus.OPEN;
        }

        this.locationId = locationId;
        this.category = category;

        // 요청 태그와 일치하지 않는 원래 태그들 제거
        deleteStudyTagNotMatchRequestTag(tags);

        // 요청 태그들를 추가
        tags.forEach(tag -> {
            StudyTag studyTag = StudyTag.createStudyTag(tag);
            studyTag.setStudy(this);
        });
    }

    private void deleteStudyTagNotMatchRequestTag(List<Tag> tags) {
        Iterator<StudyTag> studyTagIter = studyTags.iterator();
        while (studyTagIter.hasNext()){
            StudyTag studyTag = studyTagIter.next();

            boolean matchResult = false;

            for (Tag tag : tags) {
                if (studyTag.getTag().getId().equals(tag.getId())) { // 태그의 ID값과 일치하는지 확인
                    matchResult = true;
                    tags.remove(tag); // 일치하면 추가할 스터디 태그가 아니므로 TagList에서 삭제
                    break;
                }
            }

            // 일치하지 않으면 스터디 태그에서 제거
            if (!matchResult) {
                studyTagIter.remove();
            }
        }
    }
}
