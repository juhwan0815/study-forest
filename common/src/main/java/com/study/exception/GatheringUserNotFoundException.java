package com.study.exception;

public class GatheringUserNotFoundException extends RuntimeException{

    public GatheringUserNotFoundException() {
        super();
    }

    public GatheringUserNotFoundException(String message) {
        super(message);
    }
}
