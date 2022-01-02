package com.study.exception;

public class UserDuplicateException extends RuntimeException{

    public UserDuplicateException() {
        super();
    }

    public UserDuplicateException(String message) {
        super(message);
    }
}
