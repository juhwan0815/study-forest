package com.study.userservice.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class InterestTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_tag_id")
    private Long id;

    private Long tagId;

    private String tagName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static InterestTag createInterestTag(Long tagId,String tagName,User user){
        InterestTag interestTag = new InterestTag();
        interestTag.tagId = tagId;
        interestTag.tagName = tagName;
        interestTag.user = user;
        return interestTag;
    }
}