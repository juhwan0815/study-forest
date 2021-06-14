package com.study.userservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // 회원 Id

    @Column(unique = true)
    private Long kakaoId; // 카카오 Id

    private String nickName; // 닉네임

    private String thumbnailImage; // 썸네일 이미지

    private String profileImage; // 프로필 이미지

    private String refreshToken; // Refresh 토큰

    private String imageStoreName; // 프로필 이미지 저장이름

    private String ageRange; // 나이대

    private String gender; // 성별

    @Enumerated(EnumType.STRING)
    private UserStatus status; // 회원 상태

    @Enumerated(EnumType.STRING)
    private UserRole role; // 회원 권한

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<StudyJoin> studyJoins = new ArrayList<>();

    public static User createUser(Long kakaoId, String nickName,
                                  String thumbnailImage, String profileImage,
                                  String ageRange,
                                  String gender,
                                  UserRole role){
        User user = new User();
        user.kakaoId = kakaoId;
        user.nickName = nickName;
        user.thumbnailImage = thumbnailImage;
        user.profileImage = profileImage;
        user.ageRange = ageRange;
        user.gender = gender;
        user.status = UserStatus.ACTIVE;
        user.role = role;
        return user;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void logout() {
        this.refreshToken = null;
    }

    public void changeNickName(String nickName){
        this.nickName = nickName;
    }

    public void deleteImage(){
        this.profileImage = null;
        this.thumbnailImage = null;
        this.imageStoreName = null;
    }

    public void changeImage(String profileImage, String thumbnailImage, String imageStoreName){
        this.profileImage = profileImage;
        this.thumbnailImage = thumbnailImage;
        this.imageStoreName = imageStoreName;
    }

    public void addStudyJoin(Long studyId) {
        StudyJoin studyJoin = StudyJoin.createStudyJoin(studyId, this);
        studyJoins.add(studyJoin);
    }

    public void failStudyJoin(Long studyId) {
        List<StudyJoin> matchStudyJoins = studyJoins.stream()
                .filter(studyJoin -> studyJoin.getStudyId().equals(studyId))
                .sorted(Comparator.comparing(StudyJoin::getId).reversed())
                .collect(Collectors.toList());

        StudyJoin studyJoin = matchStudyJoins.get(0);

        studyJoin.fail();
    }

    public void successStudyJoin(Long studyId) {
        List<StudyJoin> matchStudyJoins = studyJoins.stream()
                .filter(studyJoin -> studyJoin.getStudyId().equals(studyId))
                .sorted(Comparator.comparing(StudyJoin::getId).reversed())
                .collect(Collectors.toList());

        StudyJoin studyJoin = matchStudyJoins.get(0);
        studyJoin.success();
    }
}
