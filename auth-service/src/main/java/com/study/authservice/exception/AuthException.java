package com.study.authservice.exception;

public class AuthException extends RuntimeException{

    public AuthException() {
        super();
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }
}
