package com.study.keyword.controller;

import com.study.config.LoginUser;
import com.study.keyword.dto.KeywordCreateRequest;
import com.study.keyword.dto.KeywordResponse;
import com.study.keyword.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @DeleteMapping("/api/keywords/{keywordId}")
    public ResponseEntity<Void> delete(@LoginUser Long userId, @PathVariable Long keywordId) {
        keywordService.delete(userId, keywordId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/api/keywords")
    public ResponseEntity<List<KeywordResponse>> findByUserId(@LoginUser Long userId) {
        return ResponseEntity.ok(keywordService.findByUserId(userId));
    }



}
