package com.study.chatroom;


import com.study.common.BaseEntity;
import com.study.study.Study;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    public static ChatRoom createChatRoom(String name, Study study){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.name = name;
        chatRoom.study = study;
        return chatRoom;
    }

    public void changeName(String name) {
        this.name = name;
    }
}