package com.study.exception;

public class NotStudyAdminException extends RuntimeException{

    public NotStudyAdminException(String message) {
        super(message);
    }
}
