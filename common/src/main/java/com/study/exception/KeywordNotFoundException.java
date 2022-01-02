package com.study.exception;

public class KeywordNotFoundException extends RuntimeException{

    public KeywordNotFoundException() {
        super();
    }

    public KeywordNotFoundException(String message) {
        super(message);
    }
}
