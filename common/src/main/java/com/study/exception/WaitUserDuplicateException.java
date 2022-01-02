package com.study.exception;

public class WaitUserDuplicateException extends RuntimeException {

    public WaitUserDuplicateException(String message) {
        super(message);
    }
}
