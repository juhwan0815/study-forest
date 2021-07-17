package com.study.gatheringservice.domain;

import com.study.gatheringservice.exception.GatheringException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        Optional<GatheringUser> optionalGatheringUser = gatheringUsers.stream()
                .filter(gatheringUser -> gatheringUser.getUserId().equals(userId))
                .findAny();

        if(optionalGatheringUser.isPresent()){
            throw new GatheringException("이미 모임에 참가한 유저입니다.");
        }

        GatheringUser gatheringUser = GatheringUser.createGatheringUser(userId, register, this);
        gatheringUsers.add(gatheringUser);
        numberOfPeople += 1;
    }

    public void checkRegister(Long userId) {
        boolean checkResult = gatheringUsers.stream()
                .anyMatch(gatheringUser ->
                        gatheringUser.getUserId().equals(userId) && gatheringUser.getRegister().equals(true));

        if(!checkResult){
            throw new GatheringException("모임을 수정할 권한이 없습니다.");
        }
    }

    public void update(LocalDateTime gatheringTime, Shape shape, String content) {
        this.gatheringTime = gatheringTime;
        this.shape = shape;
        this.content = content;
    }
}
