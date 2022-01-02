package com.study.exception;

public class ChatRoomDuplicateException extends RuntimeException{

    public ChatRoomDuplicateException(String message) {
        super(message);
    }
}
