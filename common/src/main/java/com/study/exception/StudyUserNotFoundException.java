package com.study.exception;

public class StudyUserNotFoundException extends RuntimeException {

    public StudyUserNotFoundException(String message) {
        super(message);
    }
}
