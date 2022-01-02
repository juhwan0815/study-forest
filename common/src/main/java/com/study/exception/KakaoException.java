package com.study.exception;

public class KakaoException extends RuntimeException{

    public KakaoException() {
        super();
    }

    public KakaoException(String message) {
        super(message);
    }
}
