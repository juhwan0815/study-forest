package com.study.exception;

public class WaitUserNotFoundException extends RuntimeException {

    public WaitUserNotFoundException(String message) {
        super(message);
    }
}
