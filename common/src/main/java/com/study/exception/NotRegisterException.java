package com.study.exception;

public class NotRegisterException extends RuntimeException {

    public NotRegisterException() {
        super();
    }

    public NotRegisterException(String message) {
        super(message);
    }
}
