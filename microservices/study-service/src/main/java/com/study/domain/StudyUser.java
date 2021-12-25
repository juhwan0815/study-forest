package com.study.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_user_id")
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private StudyRole studyRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public static StudyUser createStudyUser(Long userId, StudyRole studyRole, Study study) {
        StudyUser studyUser = new StudyUser();
        studyUser.userId = userId;
        studyUser.studyRole = studyRole;
        studyUser.study = study;
        return studyUser;
    }
}
