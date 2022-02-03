package com.study.keyword.controller;

import com.study.config.LoginUser;
import com.study.keyword.dto.KeywordCreateRequest;
import com.study.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @PostMapping("/api/keywords")
    public ResponseEntity<Void> create(@LoginUser Long userId,
                                       @RequestBody @Valid KeywordCreateRequest request) {
        keywordService.create(userId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
