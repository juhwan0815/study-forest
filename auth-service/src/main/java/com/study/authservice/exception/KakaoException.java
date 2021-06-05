package com.study.authservice.exception;

public class KakaoException extends RuntimeException {
    public KakaoException() {
        super();
    }

    public KakaoException(String message) {
        super(message);
    }

    public KakaoException(Throwable cause) {
        super(cause);
    }
}
