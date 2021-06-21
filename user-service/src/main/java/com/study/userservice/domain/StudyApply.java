package com.study.userservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StudyApply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_apply_id")
    private Long id;

    private Long studyId;

    @Enumerated(EnumType.STRING)
    private StudyApplyStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static StudyApply createStudyApply(Long studyId,User user){
        StudyApply studyApply = new StudyApply();
        studyApply.studyId = studyId;
        studyApply.user = user;
        studyApply.status = StudyApplyStatus.WAIT;
        return studyApply;
    }

    public void fail(){
        this.status = StudyApplyStatus.FAIL;
    }

    public void success(){
        this.status = StudyApplyStatus.SUCCESS;
    }
}
