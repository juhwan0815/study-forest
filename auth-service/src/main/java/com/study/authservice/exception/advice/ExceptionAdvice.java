package com.study.authservice.exception.advice;

import com.study.authservice.exception.AuthException;
import com.study.authservice.exception.KakaoException;
import com.study.authservice.model.common.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handlerValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(ExceptionResponse.from("잘못된 요청입니다.",errors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> handlerValidationExceptions(BindException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(ExceptionResponse.from("잘못된 요청입니다.",errors));
    }

    @ExceptionHandler(KakaoException.class)
    public ResponseEntity<ExceptionResponse> kakaoExceptionHandling(KakaoException ex){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ExceptionResponse.from(ex.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> AuthExceptionHandling(AuthException ex){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ExceptionResponse.from(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse exceptionResponse(Exception e){
        ExceptionResponse exceptionResponse = ExceptionResponse.from(e.getMessage());
        return exceptionResponse;
    }

}
