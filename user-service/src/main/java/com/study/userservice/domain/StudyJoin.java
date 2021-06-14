package com.study.userservice.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyJoin extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_join_id")
    private Long id;

    private Long studyId;

    @Enumerated(EnumType.STRING)
    private StudyJoinStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static StudyJoin createStudyJoin(Long studyId,User user){
        StudyJoin studyJoin = new StudyJoin();
        studyJoin.studyId = studyId;
        studyJoin.user = user;
        studyJoin.status = StudyJoinStatus.WAIT;
        return studyJoin;
    }

    public static StudyJoin createTestStudyJoin(Long id,Long studyId,User user){
        StudyJoin studyJoin = new StudyJoin();
        studyJoin.id = id;
        studyJoin.studyId = studyId;
        studyJoin.user = user;
        studyJoin.status = StudyJoinStatus.WAIT;
        return studyJoin;
    }

    public void fail(){
        this.status = StudyJoinStatus.FAIL;
    }

    public void success(){
        this.status = StudyJoinStatus.SUCCESS;
    }
}
