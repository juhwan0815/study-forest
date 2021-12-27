package com.study.domain;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private boolean offline;

    private String content;

    private Integer numberOfPeople;

    @Embedded
    private Place place;

    @OneToMany(mappedBy = "gathering", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GatheringUser> gatheringUsers = new ArrayList<>();

    public static Gathering createGathering(Long studyId, LocalDateTime gatheringTime, boolean status, String content) {
        Gathering gathering = new Gathering();
        gathering.studyId = studyId;
        gathering.gatheringTime = gatheringTime;
        gathering.status = status;
        gathering.content = content;
        gathering.numberOfPeople = 0;
        return gathering;
    }

    public void update(LocalDateTime gatheringTime, boolean status, String content) {
        this.gatheringTime = gatheringTime;
        this.status = status;
        this.content = content;
    }

    public void changePlace(Place place) {
        this.place = place;
    }

    public void addGatheringUser(Long userId, Boolean register) {
        Optional<GatheringUser> optionalGatheringUser = gatheringUsers.stream()
                .filter(gatheringUser -> gatheringUser.getUserId().equals(userId))
                .findAny();

        if (optionalGatheringUser.isPresent()) {
            throw new RuntimeException("이미 모임에 참가한 유저입니다.");
        }

        GatheringUser gatheringUser = GatheringUser.createGatheringUser(userId, register, this);
        gatheringUsers.add(gatheringUser);
        numberOfPeople += 1;
    }

    public void deleteGatheringUser(Long userId) {
        GatheringUser findGatheringUser = gatheringUsers.stream()
                .filter(gatheringUser -> gatheringUser.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new RuntimeException(userId + "는 존재하지 않는 참가 회원입니다."));

        gatheringUsers.remove(findGatheringUser);
        this.numberOfPeople -= 1;
    }

    public void isRegister(Long userId) {
        boolean result = gatheringUsers.stream()
                .anyMatch(gatheringUser ->
                        gatheringUser.getUserId().equals(userId) && gatheringUser.getRegister().equals(true));

        if (!result) {
            throw new RuntimeException("모임을 수정할 권한이 없습니다.");
        }
    }

    public List<Long> getGatheringUserId(){
        List<Long> userIds = gatheringUsers.stream()
                .map(gatheringUser -> gatheringUser.getUserId())
                .collect(Collectors.toList());
        return userIds;
    }

}
