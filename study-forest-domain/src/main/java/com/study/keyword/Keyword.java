package com.study.keyword;

import com.study.common.BaseEntity;
import com.study.user.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_keyword_id")
    private Long id;

    private String content; // 키워드 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Keyword createKeyword(String content, User user) {
        Keyword keyword = new Keyword();
        keyword.content = content;
        keyword.user = user;
        return keyword;
    }
}
