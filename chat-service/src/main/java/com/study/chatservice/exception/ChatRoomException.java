package com.study.chatservice.exception;

public class ChatRoomException extends RuntimeException{

    public ChatRoomException() {
        super();
    }

    public ChatRoomException(String message) {
        super(message);
    }

    public ChatRoomException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatRoomException(Throwable cause) {
        super(cause);
    }
}
