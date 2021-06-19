package com.study.studyservice.domain;

import com.study.studyservice.exception.StudyException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @Embedded
    private Image image;

    private Long locationId; // 지역정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyTag> studyTags = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaitUser> waitUsers = new ArrayList<>();

    public static Study createStudy(String name, Integer numberOfPeople, String content, boolean online,
                                    boolean offline, Category category) {
        Study study = new Study();
        study.name = name;
        study.numberOfPeople = numberOfPeople;
        study.currentNumberOfPeople = 0;
        study.content = content;
        study.online = online;
        study.offline = offline;
        study.status = StudyStatus.OPEN;
        study.category = category;
        return study;
    }

    public void addStudyUser(Long userId,Role role){
        if(this.status.equals(StudyStatus.CLOSE)){
            throw new StudyException("스터디가 마감되었습니다.");
        }

        StudyUser studyUser = StudyUser.createStudyUser(userId, role, this);
        studyUsers.add(studyUser);
        this.currentNumberOfPeople += 1;

        if(currentNumberOfPeople == numberOfPeople){
            this.status = StudyStatus.CLOSE;
        }
    }

    public void changeImage(Image image){
        this.image = image;
    }

    public void changeLocation(Long locationId) {
        this.locationId = locationId;
    }

    public void addStudyTags(List<Tag> tags){
        tags.forEach(tag -> {
            StudyTag studyTag = StudyTag.createStudyTag(tag, this);
            studyTags.add(studyTag);
        });
    }

    public void checkNumberOfStudyUser(Integer numberOfPeople) {
        if (currentNumberOfPeople > numberOfPeople) {
            throw new StudyException("현재 스터디 인원이 수정할 인원보다 많습니다.");
        }
    }

    public void update(String name, Integer numberOfPeople, String content, boolean online, boolean offline,
                       boolean close, Category category) {
        this.name = name;
        this.numberOfPeople = numberOfPeople;
        this.content = content;
        this.online = online;
        this.offline = offline;
        if (close) {
            this.status = StudyStatus.CLOSE;
        }else {
            this.status = StudyStatus.OPEN;
        }
        this.category = category;
    }

    public void updateStudyTags(List<Tag> tags){
        deleteStudyTags(tags);
        addStudyTags(tags);
    }

    private void deleteStudyTags(List<Tag> tags) {
        Iterator<StudyTag> studyTagIter = studyTags.iterator();

        while (studyTagIter.hasNext()){
            StudyTag studyTag = studyTagIter.next();

            boolean matchResult = false;

            for (Tag tag : tags) {
                if (studyTag.getTag().getId().equals(tag.getId())) {
                    matchResult = true;
                    tags.remove(tag);
                    break;
                }
            }

            if (!matchResult) {
                studyTagIter.remove();
            }
        }
    }

    public void checkStudyAdmin(Long userId) {
        boolean checkResult = studyUsers
                .stream()
                .anyMatch(studyUser ->
                        studyUser.getUserId().equals(userId) && studyUser.getRole().equals(Role.ADMIN));

        if(!checkResult){
            throw new StudyException("스터디를 수정할 권한이 없습니다.");
        }
    }

    public void checkExistWaitUserAndStudyUser(Long userId) {
        boolean checkWaitUserResult = waitUsers.stream()
                .anyMatch(waitUser -> waitUser.getUserId().equals(userId));

        if(checkWaitUserResult){
            throw new StudyException("이미 참가 신청을 한 회원입니다.");
        }

        boolean studyUserCheckResult = studyUsers.stream()
                .anyMatch(studyUser -> studyUser.getUserId().equals(userId));
        if (studyUserCheckResult){
            throw new StudyException("이미 스터디에 가입하신 회원입니다.");
        }
    }

    public void addWaitUser(Long userId) {
        WaitUser waitUser = WaitUser.createWaitUser(userId,this);
        waitUsers.add(waitUser);
    }

    public void deleteWaitUser(Long userId) {
        WaitUser findWaitUser = waitUsers.stream()
                .filter(waitUser -> waitUser.getUserId().equals(userId))
                .findFirst().get();
        waitUsers.remove(findWaitUser);
    }

}
