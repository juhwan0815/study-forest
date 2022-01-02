package com.study.exception;

public class KeywordDuplicateException extends RuntimeException {

    public KeywordDuplicateException() {
        super();
    }

    public KeywordDuplicateException(String message) {
        super(message);
    }
}
