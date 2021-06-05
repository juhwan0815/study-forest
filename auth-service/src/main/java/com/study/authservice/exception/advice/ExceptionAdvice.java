package com.study.authservice.exception.advice;

import com.study.authservice.exception.KakaoException;
import com.study.authservice.model.common.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(KakaoException.class)
    public ResponseEntity<ExceptionResponse> kakaoExceptionHandling(KakaoException ex){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.from(ex.getMessage()));
    }




}
