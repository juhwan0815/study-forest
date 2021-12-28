package com.study.domain;

import com.study.dto.chatroom.ChatRoomCreateRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Embedded
    private Image image;

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
                                    boolean online, boolean offline, Category category) {
        Study study = new Study();
        study.name = name;
        study.content = content;
        study.numberOfPeople = numberOfPeople;
        study.currentNumberOfPeople = 0;
        study.online = online;
        study.offline = offline;
        study.status = StudyStatus.OPEN;
        study.category = category;
        return study;
    }

    public void change(String name, String content, int numberOfPeople,
                       boolean online, boolean offline, boolean open, Category category) {
        this.name = name;
        this.content = content;
        this.numberOfPeople = numberOfPeople;

        if (currentNumberOfPeople > numberOfPeople) {
            throw new RuntimeException();
        }

        this.online = online;
        this.offline = offline;
        if (offline) {
            this.areaId = null;
        }

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

    public void changeImage(Image image) {
        this.image = image;
    }

    public void addTags(List<String> requestTags) {
        List<Tag> tags = requestTags.stream()
                .map(content -> Tag.createTag(content, this))
                .collect(Collectors.toList());

        this.tags.addAll(tags);
    }

    public void changeArea(Long areaId) {
        this.areaId = areaId;
    }

    public void isStudyAdmin(Long userId) {
        boolean checkResult = studyUsers.stream()
                .anyMatch(studyUser -> studyUser.getUserId().equals(userId) && studyUser.getStudyRole().equals(StudyRole.ADMIN));
        if (!checkResult) {
            throw new RuntimeException();
        }
    }

    public void addWaitUser(Long userId) {

        boolean waitUserResult = waitUsers.stream()
                .anyMatch(waitUser -> waitUser.getUserId().equals(userId));
        if (waitUserResult) {
            throw new RuntimeException();
        }

        boolean studyUserResult = studyUsers.stream()
                .anyMatch(studyUser -> studyUser.getUserId().equals(userId));
        if (studyUserResult) {
            throw new RuntimeException();
        }

        WaitUser waitUser = WaitUser.createWaitUser(userId, this);
        waitUsers.add(waitUser);
    }

    public void failWaitUser(Long userId) {
        WaitUser findWaitUser = waitUsers.stream()
                .filter(waitUser -> waitUser.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new RuntimeException());

        findWaitUser.fail();
    }

    public void deleteWaitUser(Long userId) {
        WaitUser findWaitUser = waitUsers.stream()
                .filter(waitUser -> waitUser.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new RuntimeException());

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
                .findFirst().orElseThrow(() -> new RuntimeException());

        findWaitUser.success();
    }

    public void deleteStudyUser(Long studyUserId) {
        StudyUser findStudyUser = studyUsers.stream()
                .filter(studyUser -> studyUser.getUserId().equals(studyUserId))
                .findFirst().orElseThrow(() -> new RuntimeException());

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
            throw new RuntimeException();
        }
        ChatRoom chatRoom = ChatRoom.createChatRoom(name, this);
        chatRooms.add(chatRoom);
    }

    public void updateChatRoom(Long chatRoomId, String name) {
        ChatRoom findChatRoom = chatRooms.stream()
                .filter(chatRoom -> chatRoom.getId().equals(chatRoomId))
                .findFirst().orElseThrow(() -> new RuntimeException());

        boolean chatRoomResult = chatRooms.stream()
                .anyMatch(chatRoom -> chatRoom.getName().equals(name));

        if (chatRoomResult) {
            throw new RuntimeException();
        }

        findChatRoom.changeName(name);
    }

    public void deleteChatRoom(Long chatRoomId) {
        ChatRoom findChatRoom = chatRooms.stream()
                .filter(chatRoom -> chatRoom.getId().equals(chatRoomId))
                .findFirst().orElseThrow(() -> new RuntimeException());

        chatRooms.remove(findChatRoom);
    }

    public void addTag(String content) {
        boolean tagResult = tags.stream()
                .anyMatch(tag -> tag.getContent().equals(content));
        if (tagResult) {
            throw new RuntimeException();
        }

        Tag tag = Tag.createTag(content, this);
        tags.add(tag);
    }

    public void deleteTag(Long tagId) {
        Tag findTag = tags.stream()
                .filter(tag -> tag.getId().equals(tagId))
                .findFirst().orElseThrow(() -> new RuntimeException());
        tags.remove(findTag);
    }

    public List<Long> getChatRoomsId(){
        return chatRooms.stream()
                .map(ChatRoom::getId)
                .collect(Collectors.toList());
    }
}
