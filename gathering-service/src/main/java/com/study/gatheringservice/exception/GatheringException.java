package com.study.gatheringservice.exception;

public class GatheringException extends RuntimeException{

    public GatheringException() {
        super();
    }

    public GatheringException(String message) {
        super(message);
    }

    public GatheringException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatheringException(Throwable cause) {
        super(cause);
    }
}
