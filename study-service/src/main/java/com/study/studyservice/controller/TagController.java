package com.study.studyservice.controller;

import com.study.studyservice.model.tag.TagFindRequest;
import com.study.studyservice.model.tag.TagResponse;
import com.study.studyservice.model.tag.TagSearchRequest;
import com.study.studyservice.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/tags")
    public ResponseEntity<Page<TagResponse>> findLikeName(@PageableDefault(size = 20) Pageable pageable,
                                                          @Valid TagSearchRequest request) {
        return ResponseEntity.ok(tagService.findLikeName(pageable,request));
    }

    @GetMapping("/tags/name")
    public ResponseEntity<List<TagResponse>> findByIdIn(TagFindRequest request){
        return ResponseEntity.ok(tagService.findByIdIn(request));
    }
}
