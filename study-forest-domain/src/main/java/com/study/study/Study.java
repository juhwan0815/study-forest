package com.study.study;

import com.study.area.Area;
import com.study.category.Category;
import com.study.chatroom.ChatRoom;
import com.study.common.BaseEntity;
import com.study.studyuser.StudyUser;
import com.study.waituser.WaitUser;
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

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaitUser> waitUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    public static Study createStudy(String name, String content, int numberOfPeople, boolean online, boolean offline,
                                    String imageUrl, Category category, Area area) {
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
        study.area = area;
        return study;
    }

    public void change(String name, String content, int numberOfPeople, boolean online, boolean offline, boolean open,
                       String imageUrl, Category category, Area area) {
        this.name = name;
        this.content = content;
        this.numberOfPeople = numberOfPeople;

        if (currentNumberOfPeople > numberOfPeople) {
            throw new RuntimeException("현재 인원이 더 많기 때문에 스터디 인원을 변경할 수 없습니다.");
        }

        this.online = online;
        this.offline = offline;
        this.category = category;
        this.area = area;
        this.imageUrl = imageUrl;

        if (open) {
            this.status = StudyStatus.OPEN;
        } else {
            this.status = StudyStatus.CLOSE;
        }
    }

    public void changeTags(List<String> requestTags) {
        List<Tag> tags = requestTags.stream()
                .map(content -> Tag.createTag(content, this))
                .collect(Collectors.toList());

        this.tags.clear();
        this.tags.addAll(tags);
    }


//    public void addWaitUser(User user) {
//        boolean waitUserResult = waitUsers.stream()
//                .anyMatch(waitUser -> waitUser.getUser().);
//        if (waitUserResult) {
//            throw new RuntimeException("는 이미 스터디 참가 대기 중인 회원 ID 입니다.");
//        }
//
//        boolean studyUserResult = studyUsers.stream()
//                .anyMatch(studyUser -> studyUser.getUser().getId().equals(userId));
//        if (studyUserResult) {
//            throw new RuntimeException("는 이미 스터디 참가자인 회원 ID 입니다.");
//        }
//
//        WaitUser waitUser = WaitUser.createWaitUser(user, this);
//        waitUsers.add(waitUser);
//    }
//
//    public void failWaitUser(Long userId) {
//        WaitUser findWaitUser = waitUsers.stream()
//                .filter(waitUser -> waitUser.getUserId().equals(userId))
//                .findFirst().orElseThrow(() -> new WaitUserNotFoundException(userId + "는 스터디 참가 대기자가 아닌 회원 ID 입니다."));
//
//        findWaitUser.fail();
//    }
//
//
//    public void deleteWaitUser(Long userId) {
//        WaitUser findWaitUser = waitUsers.stream()
//                .filter(waitUser -> waitUser.getUserId().equals(userId))
//                .findFirst().orElseThrow(() -> new WaitUserNotFoundException(userId + "는 스터디 참가 대기자가 아닌 회원 ID 입니다."));
//
//        waitUsers.remove(findWaitUser);
//    }
//
//    public List<Long> getWaitUsersId() {
//        List<Long> userIds = new ArrayList<>();
//        waitUsers.stream().forEach(waitUser -> {
//            if (waitUser.getStatus().equals(WaitStatus.WAIT)) {
//                userIds.add(waitUser.getUserId());
//            }
//        });
//        return userIds;
//    }
//
//    public void successWaitUser(Long userId) {
//        WaitUser findWaitUser = waitUsers.stream()
//                .filter(waitUser -> waitUser.getUserId().equals(userId))
//                .findFirst().orElseThrow(() -> new WaitUserNotFoundException(userId + "는 스터디 참가 대기자가 아닌 회원 ID 입니다."));
//
//        findWaitUser.success();
//    }
//
//    public void deleteStudyUser(Long studyUserId) {
//        StudyUser findStudyUser = studyUsers.stream()
//                .filter(studyUser -> studyUser.getUserId().equals(studyUserId))
//                .findFirst().orElseThrow(() -> new StudyUserNotFoundException(studyUserId + "는 스터디 참가자가 아닌 회원 ID 입니다."));
//
//        studyUsers.remove(findStudyUser);
//        this.currentNumberOfPeople -= 1;
//    }
//
//    public List<Long> getStudyUsersId() {
//        List<Long> userIds = studyUsers.stream()
//                .map(studyUser -> studyUser.getUserId())
//                .collect(Collectors.toList());
//        return userIds;
//    }
//
//    public void addChatRoom(String name) {
//        boolean chatRoomResult = chatRooms.stream()
//                .anyMatch(chatRoom -> chatRoom.getName().equals(name));
//        if (chatRoomResult) {
//            throw new ChatRoomDuplicateException(name + "은 이미 존재하는 채팅방입니다.");
//        }
//        ChatRoom chatRoom = ChatRoom.createChatRoom(name, this);
//        chatRooms.add(chatRoom);
//    }
//
//    public void updateChatRoom(Long chatRoomId, String name) {
//        ChatRoom findChatRoom = chatRooms.stream()
//                .filter(chatRoom -> chatRoom.getId().equals(chatRoomId))
//                .findFirst().orElseThrow(() -> new ChatRoomNotFoundException(chatRoomId + "는 존재하지 않는 채팅방 ID 입니다."));
//
//        boolean chatRoomResult = chatRooms.stream()
//                .anyMatch(chatRoom -> chatRoom.getName().equals(name));
//
//        if (chatRoomResult) {
//            throw new ChatRoomDuplicateException(name + "은 이미 존재하는 채팅방입니다.");
//        }
//
//        findChatRoom.changeName(name);
//    }
//
//    public void deleteChatRoom(Long chatRoomId) {
//        ChatRoom findChatRoom = chatRooms.stream()
//                .filter(chatRoom -> chatRoom.getId().equals(chatRoomId))
//                .findFirst().orElseThrow(() -> new ChatRoomNotFoundException(chatRoomId + "는 존재하지 않는 채팅방 ID 입니다."));
//
//        chatRooms.remove(findChatRoom);
//    }
//
//    public void addTag(String content) {
//        boolean tagResult = tags.stream()
//                .anyMatch(tag -> tag.getContent().equals(content));
//        if (tagResult) {
//            throw new TagDuplicateException(content + "는 이미 존재하는 태그입니다.");
//        }
//
//        Tag tag = Tag.createTag(content, this);
//        tags.add(tag);
//    }
//
//    public void deleteTag(Long tagId) {
//        Tag findTag = tags.stream()
//                .filter(tag -> tag.getId().equals(tagId))
//                .findFirst().orElseThrow(() -> new TagNotFoundException(tagId + "는 존재하지 않는 태그 ID 입니다."));
//        tags.remove(findTag);
//    }
//
//    public List<Long> getChatRoomsId() {
//        return chatRooms.stream()
//                .map(ChatRoom::getId)
//                .collect(Collectors.toList());
//    }
//
//    public ChatRoom getChatRoom(Long chatRoomId) {
//        return chatRooms.stream()
//                .filter(chatRoom -> chatRoom.getId().equals(chatRoomId))
//                .findFirst().orElseThrow(() -> new ChatRoomNotFoundException(chatRoomId + "는 존재하지 않는 채팅방 ID 입니다."));
//    }
}
