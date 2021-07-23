package com.study.chatservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoom extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    private String name;

    private Long studyId;

    public static ChatRoom createChatRoom(String name,Long studyId){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.name = name;
        chatRoom.studyId = studyId;
        return chatRoom;
    }
}
