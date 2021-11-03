package com.study.chatservice.model.chatroom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomCreateRequest {

    @NotBlank(message = "채팅방 이름은 필수입니다.")
    private String name;

}
