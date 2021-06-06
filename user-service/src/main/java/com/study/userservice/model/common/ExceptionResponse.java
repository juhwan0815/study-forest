package com.study.userservice.model.common;

import lombok.Data;

@Data
public class ExceptionResponse {

    private String message;

    public static ExceptionResponse from(String message){
        ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.message = message;
        return exceptionResponse;
    }
}
