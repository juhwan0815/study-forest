package com.study.keyword.service;

import com.study.common.NotFoundException;
import com.study.keyword.Keyword;
import com.study.keyword.KeywordRepository;
import com.study.keyword.dto.KeywordCreateRequest;
import com.study.user.User;
import com.study.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.study.common.NotFoundException.KEYWORD_NOT_FOUND;
import static com.study.common.NotFoundException.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KeywordService {

    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public void create(Long userId, KeywordCreateRequest request) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

        Keyword keyword = Keyword.createKeyword(request.getContent(), findUser);
        keywordRepository.save(keyword);
    }

    @Transactional
    public void delete(Long userId, Long keywordId) {
        Keyword findKeyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new NotFoundException(KEYWORD_NOT_FOUND));

        if (findKeyword.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("");
        }

        keywordRepository.delete(findKeyword);
    }
}
