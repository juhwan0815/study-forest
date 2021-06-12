package com.study.studyservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity{

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

    @OneToMany(mappedBy = "study",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<StudyTag> studyTags = new ArrayList<>();

    @OneToMany(mappedBy = "study",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<WaitUser> waitUserList = new ArrayList<>();

    public static Study createStudy(String name,Integer numberOfPeople,String content,boolean online,
                                    boolean offline,String imageStoreName,String studyImage,
                                    String studyThumbnailImage,Long locationId,
                                    Category category,StudyUser user,List<StudyTag> studyTags) {
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

    public void plusCurrentNumberOfPeople(){
        this.currentNumberOfPeople += 1;
    }


}
