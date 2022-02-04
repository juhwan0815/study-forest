package com.study.studyuser;

import com.study.study.Study;
import com.study.user.User;
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

    @Enumerated(EnumType.STRING)
    private StudyRole studyRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public static StudyUser createStudyUser(StudyRole studyRole, User user, Study study) {
        StudyUser studyUser = new StudyUser();
        studyUser.studyRole = studyRole;
        studyUser.user = user;
        studyUser.study = study;
        return studyUser;
    }
}
