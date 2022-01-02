package com.study.exception;

public class StudyNotFoundException extends RuntimeException {

    public StudyNotFoundException(String message) {
        super(message);
    }
}
