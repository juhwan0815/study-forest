package com.study.domain;


import com.study.exception.GatheringNotFoundException;
import com.study.exception.GatheringUserDuplicateException;
import com.study.exception.GatheringUserNotFoundException;
import com.study.exception.NotRegisterException;
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

    public static Gathering createGathering(Long studyId, LocalDateTime gatheringTime, boolean offline, String content) {
        Gathering gathering = new Gathering();
        gathering.studyId = studyId;
        gathering.gatheringTime = gatheringTime;
        gathering.offline = offline;
        gathering.content = content;
        gathering.numberOfPeople = 0;
        return gathering;
    }

    public void update(LocalDateTime gatheringTime, boolean offline, String content) {
        this.gatheringTime = gatheringTime;
        this.offline = offline;
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
            throw new GatheringUserDuplicateException(userId + "??? ?????? ????????? ????????? ?????? ID ?????????.");
        }

        GatheringUser gatheringUser = GatheringUser.createGatheringUser(userId, register, this);
        gatheringUsers.add(gatheringUser);
        numberOfPeople += 1;
    }

    public void deleteGatheringUser(Long userId) {
        GatheringUser findGatheringUser = gatheringUsers.stream()
                .filter(gatheringUser -> gatheringUser.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new GatheringUserNotFoundException(userId + "??? ???????????? ?????? ?????? ?????? ID ?????????."));

        gatheringUsers.remove(findGatheringUser);
        this.numberOfPeople -= 1;
    }

    public void isRegister(Long userId) {
        boolean result = gatheringUsers.stream()
                .anyMatch(gatheringUser ->
                        gatheringUser.getUserId().equals(userId) && gatheringUser.isRegister());

        if (!result) {
            throw new NotRegisterException(userId + "??? ????????? ???????????? ?????? ?????? ?????? ID ?????????");
        }
    }

    public List<Long> getGatheringUserId(){
        List<Long> userIds = gatheringUsers.stream()
                .map(gatheringUser -> gatheringUser.getUserId())
                .collect(Collectors.toList());
        return userIds;
    }

}
