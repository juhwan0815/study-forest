package com.study.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GatheringUser extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_user_id")
    private Long id;

    private Long userId;

    private boolean register;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id")
    private Gathering gathering;

    public static GatheringUser createGatheringUser(Long userId,Boolean register,Gathering gathering){
        GatheringUser gatheringUser = new GatheringUser();
        gatheringUser.userId = userId;
        gatheringUser.register = register;
        gatheringUser.gathering = gathering;
        return gatheringUser;
    }
}
