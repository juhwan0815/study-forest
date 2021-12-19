package com.study.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserKeyword extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_keyword_id")
    private Long id;

    private String content; // 키워드 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static UserKeyword createUserKeyword(String content, User user) {
        UserKeyword userKeyword = new UserKeyword();
        userKeyword.content = content;
        userKeyword.user = user;
        return userKeyword;
    }
}
