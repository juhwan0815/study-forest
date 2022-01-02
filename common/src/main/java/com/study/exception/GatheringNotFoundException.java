package com.study.exception;

public class GatheringNotFoundException extends RuntimeException{

    public GatheringNotFoundException() {
    }

    public GatheringNotFoundException(String message) {
        super(message);
    }
}
