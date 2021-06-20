package com.study.studyservice.service;

import com.study.studyservice.domain.Tag;
import com.study.studyservice.model.tag.TagResponse;
import com.study.studyservice.model.tag.TagSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TagService {

    List<Tag> FindAndCreate(List<String> requestTags);

    Page<TagResponse> findLikeName(Pageable pageable, TagSearchRequest request);
}
