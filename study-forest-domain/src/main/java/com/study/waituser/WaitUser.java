package com.study.waituser;

import com.study.study.Study;
import com.study.user.User;
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

    @Enumerated(EnumType.STRING)
    private WaitStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public static WaitUser createWaitUser(User user, Study study) {
        WaitUser waitUser = new WaitUser();
        waitUser.user = user;
        waitUser.study = study;
        waitUser.status = WaitStatus.WAIT;
        return waitUser;
    }

    public void fail() {
        this.status = WaitStatus.FAIL;
    }

    public void success() {
        this.status = WaitStatus.SUCCESS;
    }
}
