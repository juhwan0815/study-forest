package com.study.userKeyword;

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
public class UserKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_keyword_id")
    private Long id;

    private String content; // 키워드 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static UserKeyword createKeyword(String content, User user) {
        UserKeyword keyword = new UserKeyword();
        keyword.content = content;
        keyword.user = user;
        return keyword;
    }
}
