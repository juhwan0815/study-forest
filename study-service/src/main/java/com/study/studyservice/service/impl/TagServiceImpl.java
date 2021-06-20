package com.study.studyservice.service.impl;

import com.study.studyservice.domain.Tag;
import com.study.studyservice.model.tag.TagFindRequest;
import com.study.studyservice.model.tag.TagResponse;
import com.study.studyservice.model.tag.TagSearchRequest;
import com.study.studyservice.repository.TagRepository;
import com.study.studyservice.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Page<TagResponse> findLikeName(Pageable pageable, TagSearchRequest request) {
        Page<Tag> tags = tagRepository.findByNameContaining(pageable, request.getName());
        return tags.map(tag -> TagResponse.from(tag));
    }

    @Override
    public List<TagResponse> findByIdIn(TagFindRequest request) {
        return tagRepository.findByIdIn(request.getTagIdList()).stream()
                .map(tag -> TagResponse.from(tag))
                .collect(Collectors.toList());
    }
}
