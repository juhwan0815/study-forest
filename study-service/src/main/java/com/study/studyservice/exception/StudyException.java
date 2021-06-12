package com.study.studyservice.exception;

public class StudyException extends RuntimeException{
    public StudyException() {
        super();
    }

    public StudyException(String message) {
        super(message);
    }

    public StudyException(Throwable cause) {
        super(cause);
    }
}
