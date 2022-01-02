package com.study.exception;

public class StudyUserDuplicateException extends RuntimeException{

    public StudyUserDuplicateException(String message) {
        super(message);
    }
}
