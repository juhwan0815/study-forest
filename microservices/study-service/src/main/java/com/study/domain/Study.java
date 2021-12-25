package com.study.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_id")
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    private int numberOfPeople;

    private int currentNumberOfPeople;

    private boolean offline;

    private boolean online;

    @Enumerated(EnumType.STRING)
    private StudyStatus status;

    @Embedded
    private Image image;

    private Long areaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyUser> studyUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WaitUser> waitUsers = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    public static Study createStudy(String name, String content, int numberOfPeople,
                                    boolean online, boolean offline, Category category) {
        Study study = new Study();
        study.name = name;
        study.content = content;
        study.numberOfPeople = numberOfPeople;
        study.currentNumberOfPeople = 0;
        study.online = online;
        study.offline = offline;
        study.status = StudyStatus.OPEN;
        study.category = category;
        return study;
    }

    public void addStudyUser(Long userId, StudyRole studyRole) {
        StudyUser studyUser = StudyUser.createStudyUser(userId, studyRole, this);
        studyUsers.add(studyUser);
        this.currentNumberOfPeople += 1;
    }

    public void changeImage(Image image) {
        this.image = image;
    }

    public void addTags(List<String> requestTags) {
        List<Tag> tags = requestTags.stream()
                .map(content -> Tag.createTag(content, this))
                .collect(Collectors.toList());

        this.tags.addAll(tags);
    }

    public void changeArea(Long areaId) {
        this.areaId = areaId;
    }
}
