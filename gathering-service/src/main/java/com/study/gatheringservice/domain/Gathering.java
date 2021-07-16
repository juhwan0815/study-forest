package com.study.gatheringservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gathering extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gathering_id")
    private Long id;

    private Long studyId;

    private LocalDateTime gatheringTime;

    @Enumerated(EnumType.STRING)
    private Shape shape;

    private String content;

    private Integer numberOfPeople;

    @Embedded
    private Place place;

    @OneToMany(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GatheringUser> gatheringUsers = new ArrayList<>();

    public static Gathering createGathering(Long studyId, LocalDateTime gatheringTime, Shape shape, String content) {
        Gathering gathering = new Gathering();
        gathering.studyId = studyId;
        gathering.gatheringTime = gatheringTime;
        gathering.shape = shape;
        gathering.content = content;
        gathering.numberOfPeople = 0;
        return gathering;
    }

    public void changePlace(Place place) {
        this.place = place;
    }

    public void addGatheringUser(Long userId,Boolean register){
        GatheringUser gatheringUser = GatheringUser.createGatheringUser(userId, register, this);
        gatheringUsers.add(gatheringUser);
        numberOfPeople += 1;
    }
}
