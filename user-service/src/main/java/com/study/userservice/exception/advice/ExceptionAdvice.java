package com.study.userservice.exception.advice;

import com.study.userservice.exception.UserException;
import com.study.userservice.model.common.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse> userExceptionHandling(UserException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.from(ex.getMessage()));
    }
}
