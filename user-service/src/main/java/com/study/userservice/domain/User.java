package com.study.userservice.domain;

import com.study.userservice.exception.UserException;
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

    private Integer searchDistance;

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
        user.searchDistance = 3;
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

    public void checkExistTag(Long tagId) {
        boolean checkTagResult = interestTags.stream()
                .anyMatch(interestTag -> interestTag.getTagId().equals(tagId));

        if(checkTagResult){
            throw new UserException("이미 관심 주제로 추가한 태그 입니다.");
        }
    }

    public void addInterestTag(Long tagId){
        InterestTag interestTag = InterestTag.createInterestTag(tagId, this);
        interestTags.add(interestTag);
    }

    public void deleteInterestTag(Long tagId){
        InterestTag findInterestTag = interestTags.stream()
                .filter(interestTag -> interestTag.getTagId().equals(tagId))
                .findFirst().orElseThrow(() -> new UserException("존재하지 않는 관심 태그입니다."));

        interestTags.remove(findInterestTag);
    }

    public void addStudyApply(Long studyId) {
        StudyApply studyApply = StudyApply.createStudyApply(studyId, this);
        studyApplies.add(studyApply);
        this.numberOfStudyApply += 1;
    }

    public void failStudyApply(Long studyId) {
        StudyApply matchStudyApply = studyApplies.stream()
                .filter(studyApply -> studyApply.getStudyId().equals(studyId))
                .findFirst().get();

        matchStudyApply.fail();
    }

    public void successStudyApply(Long studyId) {
        StudyApply matchStudyApply = studyApplies.stream()
                .filter(studyApply -> studyApply.getStudyId().equals(studyId))
                .findFirst().get();

        matchStudyApply.success();
    }

    public void deleteStudyApply(){
        Iterator<StudyApply> studyApplyIter = studyApplies.iterator();
        while (studyApplyIter.hasNext()){
            StudyApply studyApply = studyApplyIter.next();

            if(studyApply.getStatus().equals(StudyApplyStatus.FAIL)
                    || studyApply.getStatus().equals(StudyApplyStatus.SUCCESS)){
                studyApplyIter.remove();
                this.numberOfStudyApply -= 1;
            }
        }
    }

    public void changeSearchDistance(Integer searchDistance) {
        this.searchDistance = searchDistance;
    }

    public void cancelStudyApply(Long studyId) {
        StudyApply matchStudyApply = studyApplies.stream()
                .filter(studyApply -> studyApply.getStudyId().equals(studyId))
                .findFirst().get();

        studyApplies.remove(matchStudyApply);
        this.numberOfStudyApply -= 1;
    }
}
