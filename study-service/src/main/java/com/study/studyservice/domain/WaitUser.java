package com.study.studyservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public class WaitUser extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wait_user_id")
    private Long id;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public static WaitUser createWaitUser(Long userId,Study study){
        WaitUser waitUser = new WaitUser();
        waitUser.userId = userId;
        waitUser.study = study;
        return waitUser;
    }

    public static WaitUser createTestWaitUser(Long id,Long userId,Study study){
        WaitUser waitUser = new WaitUser();
        waitUser.id = id;
        waitUser.userId = userId;
        waitUser.study = study;
        return waitUser;
    }
}
