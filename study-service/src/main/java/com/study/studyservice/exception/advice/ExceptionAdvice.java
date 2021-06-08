package com.study.studyservice.exception.advice;

import com.study.studyservice.exception.CategoryException;
import com.study.studyservice.model.common.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(CategoryException.class)
    public ResponseEntity<ExceptionResponse> categoryExceptionHandling(CategoryException ex){
        return ResponseEntity.badRequest().body(ExceptionResponse.from(ex.getMessage()));
    }
}
