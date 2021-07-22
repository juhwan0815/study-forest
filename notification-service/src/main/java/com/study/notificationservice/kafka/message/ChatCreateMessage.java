package com.study.notificationservice.kafka.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCreateMessage {

    private Long studyId;

    private String chatRoomName;

    private String nickName;

    private String content;

    private List<Long> userIdList;
}
