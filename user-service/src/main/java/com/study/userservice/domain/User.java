package com.study.userservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // 회원 Id

    @Column(unique = true)
    private Long kakaoId; // 카카오 Id

    private Long locationId;

    private String nickName; // 닉네임

    private String ageRange; // 나이대

    private String gender; // 성별

    private Integer numberOfStudyApply; // 스터디 신청 내역 개수

    @Embedded
    private Image image; // 이미지 정보들

    @Enumerated(EnumType.STRING)
    private UserRole role; // 회원 권한

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<StudyApply> studyApplies = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<InterestTag> interestTags = new ArrayList<>();

    public static User createUser(Long kakaoId, String nickName, String ageRange, String gender, UserRole role){
        User user = new User();
        user.kakaoId = kakaoId;
        user.nickName = nickName;
        user.ageRange = ageRange;
        user.gender = gender;
        user.role = role;
        user.numberOfStudyApply = 0;
        return user;
    }

    public void changeImage(Image image){
        this.image = image;
    }

    public void changeNickName(String nickName){
        this.nickName = nickName;
    }

    public void changeLocation(Long locationId){
        this.locationId = locationId;
    }

    public void addInterestTag(Long tagId,String tagName){
        InterestTag interestTag = InterestTag.createInterestTag(tagId, tagName, this);
        interestTags.add(interestTag);
    }

    public void deleteInterestTag(Long interestTagId){
        Optional<InterestTag> filterResult = interestTags.stream()
                .filter(interestTag -> interestTag.getId().equals(interestTagId))
                .findFirst();

        InterestTag interestTag = filterResult.get();

        interestTags.remove(interestTag);
    }

    public void addStudyApply(Long studyId,String studyName) {
        StudyApply studyApply = StudyApply.createStudyApply(studyId,studyName, this);
        studyApplies.add(studyApply);
    }

    public void failStudyApply(Long studyId) {
        List<StudyApply> matchStudyApplies = studyApplies.stream()
                .filter(studyJoin -> studyJoin.getStudyId().equals(studyId))
                .sorted(Comparator.comparing(StudyApply::getId).reversed())
                .collect(Collectors.toList());

        StudyApply studyApply = matchStudyApplies.get(0);

        studyApply.fail();
    }

    public void successStudyApply(Long studyId) {
        List<StudyApply> matchStudyApplies = studyApplies.stream()
                .filter(studyJoin -> studyJoin.getStudyId().equals(studyId))
                .sorted(Comparator.comparing(StudyApply::getId).reversed())
                .collect(Collectors.toList());

        StudyApply studyApply = matchStudyApplies.get(0);
        studyApply.success();
    }

    public void deleteStudyApply(){
        Iterator<StudyApply> studyApplyIter = studyApplies.iterator();
        while (studyApplyIter.hasNext()){
            StudyApply studyApply = studyApplyIter.next();

            if(studyApply.getStatus().equals(StudyApplyStatus.FAIL)
                    || studyApply.getStatus().equals(StudyApplyStatus.SUCCESS)){
                studyApplyIter.remove();
            }
        }
    }
}
