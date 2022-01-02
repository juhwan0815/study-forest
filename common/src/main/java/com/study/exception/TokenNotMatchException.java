package com.study.exception;

public class TokenNotMatchException extends RuntimeException{

    public TokenNotMatchException() {
        super();
    }

    public TokenNotMatchException(String message) {
        super(message);
    }
}
