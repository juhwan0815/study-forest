package com.study.exception;

public class AreaNotFoundException extends RuntimeException {

    public AreaNotFoundException() {
        super();
    }

    public AreaNotFoundException(String message) {
        super(message);
    }
}
