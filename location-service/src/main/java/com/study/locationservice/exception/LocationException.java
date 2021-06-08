package com.study.locationservice.exception;

public class LocationException extends RuntimeException{

    public LocationException() {


    }

    public LocationException(String message) {
        super(message);
    }

    public LocationException(Throwable cause) {
        super(cause);
    }
}
