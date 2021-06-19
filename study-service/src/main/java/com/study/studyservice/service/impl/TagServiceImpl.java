package com.study.studyservice.service.impl;

import com.study.studyservice.domain.Tag;
import com.study.studyservice.repository.TagRepository;
import com.study.studyservice.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public List<Tag> FindAndCreate(List<String> requestTags) {

        List<Tag> findTags = tagRepository.findByNameIn(requestTags);

        requestTags.forEach(name -> {
            boolean matchResult = false; // false 로 설정

            for (Tag tag : findTags) {
                if (name.equals(tag.getName())) {
                    matchResult = true;
                    break;
                }
            }

            if (matchResult == false) {
                Tag savedTag = tagRepository.save(Tag.createTag(name));
                findTags.add(savedTag);
            }
        });

        return findTags;
    }
}
