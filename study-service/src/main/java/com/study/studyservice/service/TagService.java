package com.study.studyservice.service;

import com.study.studyservice.domain.Tag;

import java.util.List;

public interface TagService {

    List<Tag> FindAndCreate(List<String> requestTags);
}
