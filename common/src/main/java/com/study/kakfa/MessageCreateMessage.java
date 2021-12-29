package com.study.kakfa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageCreateMessage {

    private Long roomId;

    private String sender;

    private String content;

    private List<Long> userIds;

    public static MessageCreateMessage from(Long roomId, String sender, String content, List<Long> userIds) {
        return new MessageCreateMessage(roomId, sender, content, userIds);
    }
}
