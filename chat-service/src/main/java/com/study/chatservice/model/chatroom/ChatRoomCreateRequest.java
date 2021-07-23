package com.study.chatservice.model.chatroom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomCreateRequest {

    @NotBlank
    private String name;

}
