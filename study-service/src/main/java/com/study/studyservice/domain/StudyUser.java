package com.study.studyservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyUser extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_user_id")
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public static StudyUser createStudyUser(Long userId,Role role){
        StudyUser studyUser = new StudyUser();
        studyUser.userId = userId;
        studyUser.role = role;
        return studyUser;
    }

    public void setStudy(Study study){
        study.getStudyUsers().add(this);
        study.plusCurrentNumberOfPeople();
        this.study = study;
    }



}
