package com.study.exception;

public class AuthNotFoundException extends RuntimeException{

    public AuthNotFoundException() {
        super();
    }

    public AuthNotFoundException(String message) {
        super(message);
    }
}
