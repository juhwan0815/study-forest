package com.study.keyword.service;

import com.study.common.NotFoundException;
import com.study.keyword.Keyword;
import com.study.keyword.KeywordRepository;
import com.study.keyword.dto.KeywordCreateRequest;
import com.study.user.User;
import com.study.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KeywordService {

    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public void create(Long userId, KeywordCreateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.USER_NOT_FOUND));

        Keyword keyword = Keyword.createKeyword(request.getContent(), findUser);
        keywordRepository.save(keyword);
    }
}
