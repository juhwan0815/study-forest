package com.study.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class WaitUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wait_user_id")
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private WaitStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public static WaitUser createWaitUser(Long userId, Study study) {
        WaitUser studyUser = new WaitUser();
        studyUser.userId = userId;
        studyUser.study = study;
        studyUser.status = WaitStatus.WAIT;
        return studyUser;
    }

    public void fail() {
        this.status = WaitStatus.FAIL;
    }

    public void success() {
        this.status = WaitStatus.SUCCESS;
    }
}
