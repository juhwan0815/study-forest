package com.study.domain;

import com.study.exception.BusinessException;
import com.study.exception.DuplicateException;
import com.study.exception.NotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.study.exception.NotFoundException.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int numberOfPeople;

    private int currentNumberOfPeople;

    private boolean offline;

    private boolean online;

    @Enumerated(EnumType.STRING)
    private StudyStatus status;

    private String imageUrl;

    private Long areaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaitUser> waitUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    public static Study createStudy(String name, String content, int numberOfPeople,
                                    boolean online, boolean offline, String imageUrl, Category category) {
        Study study = new Study();
        study.name = name;
        study.content = content;
        study.numberOfPeople = numberOfPeople;
        study.currentNumberOfPeople = 0;
        study.online = online;
        study.offline = offline;
        study.status = StudyStatus.OPEN;
        study.imageUrl = imageUrl;
        study.category = category;
        return study;
    }

    public void update(String name, String content, int numberOfPeople,
                       boolean online, boolean offline, boolean open, String imageUrl, Category category) {
        this.name = name;
        this.content = content;

        if (currentNumberOfPeople > numberOfPeople) {
            throw new BusinessException("현재 인원이 더 많기 때문에 스터디 인원을 변경할 수 없습니다.");
        }
        this.numberOfPeople = numberOfPeople;


        this.online = online;
        this.offline = offline;
        this.imageUrl = imageUrl;

        if (open) {
            this.status = StudyStatus.OPEN;
        } else {
            this.status = StudyStatus.CLOSE;
        }
        this.category = category;
    }

    public void addStudyUser(Long userId, StudyRole studyRole) {
        StudyUser studyUser = StudyUser.createStudyUser(userId, studyRole, this);
        studyUsers.add(studyUser);
        this.currentNumberOfPeople += 1;
    }

    public void changeTags(List<String> requestTags) {
        List<Tag> tags = requestTags.stream()
                .map(content -> Tag.createTag(content, this))
                .collect(Collectors.toList());

        this.tags.clear();
        this.tags.addAll(tags);
    }

    public void changeArea(Long areaId) {
        this.areaId = areaId;
    }

    public boolean isStudyAdmin(Long userId) {
        return studyUsers.stream()
                .anyMatch(studyUser -> studyUser.getUserId().equals(userId) && studyUser.getStudyRole().equals(StudyRole.ADMIN));
    }

    public void addWaitUser(Long userId) {

        boolean waitUserResult = waitUsers.stream()
                .anyMatch(waitUser -> waitUser.getUserId().equals(userId));
        if (waitUserResult) {
            throw new DuplicateException("이미 스터디 참가 대기 중인 회원입니다.");
        }

        boolean studyUserResult = studyUsers.stream()
                .anyMatch(studyUser -> studyUser.getUserId().equals(userId));
        if (studyUserResult) {
            throw new DuplicateException("이미 스터디 참가자인 회원입니다.");
        }

        WaitUser waitUser = WaitUser.createWaitUser(userId, this);
        waitUsers.add(waitUser);
    }

    public void failWaitUser(Long userId) {

        WaitUser findWaitUser = waitUsers.stream()
                .filter(waitUser -> waitUser.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new NotFoundException(WAIT_USER_NOT_FOUND));

        findWaitUser.fail();
    }

    public void deleteWaitUser(Long userId) {
        WaitUser findWaitUser = waitUsers.stream()
                .filter(waitUser -> waitUser.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new NotFoundException(WAIT_USER_NOT_FOUND));

        waitUsers.remove(findWaitUser);
    }

    public List<Long> getWaitUsersId() {
        List<Long> userIds = new ArrayList<>();
        waitUsers.stream().forEach(waitUser -> {
            if (waitUser.getStatus().equals(WaitStatus.WAIT)) {
                userIds.add(waitUser.getUserId());
            }
        });
        return userIds;
    }

    public void successWaitUser(Long userId) {
        WaitUser findWaitUser = waitUsers.stream()
                .filter(waitUser -> waitUser.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new NotFoundException(WAIT_USER_NOT_FOUND));

        findWaitUser.success();
    }

    public void deleteStudyUser(Long studyUserId) {
        StudyUser findStudyUser = studyUsers.stream()
                .filter(studyUser -> studyUser.getUserId().equals(studyUserId))
                .findFirst().orElseThrow(() -> new NotFoundException(STUDY_USER_NOT_FOUND));

        studyUsers.remove(findStudyUser);
        this.currentNumberOfPeople -= 1;
    }

    public List<Long> getStudyUsersId() {
        List<Long> userIds = studyUsers.stream()
                .map(studyUser -> studyUser.getUserId())
                .collect(Collectors.toList());
        return userIds;
    }

    public void addChatRoom(String name) {
        boolean chatRoomResult = chatRooms.stream()
                .anyMatch(chatRoom -> chatRoom.getName().equals(name));

        if (chatRoomResult) {
            throw new DuplicateException(String.format("%s는 이미 존재하는 채팅방입니다.", name));
        }

        ChatRoom chatRoom = ChatRoom.createChatRoom(name, this);
        chatRooms.add(chatRoom);
    }

    public void updateChatRoom(Long chatRoomId, String name) {
        ChatRoom findChatRoom = chatRooms.stream()
                .filter(chatRoom -> chatRoom.getId().equals(chatRoomId))
                .findFirst().orElseThrow(() -> new NotFoundException(CHAT_ROOM_NOT_FOUND));

        boolean chatRoomResult = chatRooms.stream()
                .anyMatch(chatRoom -> chatRoom.getName().equals(name));

        if (chatRoomResult) {
            throw new DuplicateException(String.format("%s는 이미 존재하는 채팅방입니다.", name));
        }

        findChatRoom.changeName(name);
    }

    public void deleteChatRoom(Long chatRoomId) {
        ChatRoom findChatRoom = chatRooms.stream()
                .filter(chatRoom -> chatRoom.getId().equals(chatRoomId))
                .findFirst().orElseThrow(() -> new NotFoundException(CHAT_ROOM_NOT_FOUND));

        chatRooms.remove(findChatRoom);
    }

    public List<Long> getChatRoomsId() {
        return chatRooms.stream()
                .map(ChatRoom::getId)
                .collect(Collectors.toList());
    }

    public ChatRoom getChatRoom(Long chatRoomId) {
        return chatRooms.stream()
                .filter(chatRoom -> chatRoom.getId().equals(chatRoomId))
                .findFirst().orElseThrow(() -> new NotFoundException(CHAT_ROOM_NOT_FOUND));
    }
}
