package com.study.exception;

public class GatheringUserDuplicateException extends RuntimeException{

    public GatheringUserDuplicateException() {
        super();
    }

    public GatheringUserDuplicateException(String message) {
        super(message);
    }
}
