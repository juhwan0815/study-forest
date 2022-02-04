package com.study.study;

import com.study.area.Area;
import com.study.category.Category;
import com.study.common.BaseEntity;
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

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "study", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    public static Study createStudy(String name, String content, int numberOfPeople,
                                    boolean online, boolean offline, Category category, Area area) {
        Study study = new Study();
        study.name = name;
        study.content = content;
        study.numberOfPeople = numberOfPeople;
        study.currentNumberOfPeople = 0;
        study.online = online;
        study.offline = offline;
        study.status = StudyStatus.OPEN;
        study.category = category;
        study.area = area;
        return study;
    }

    public void change(String name, String content, int numberOfPeople,
                       boolean online, boolean offline, boolean open, Category category) {
        this.name = name;
        this.content = content;
        this.numberOfPeople = numberOfPeople;

        if (currentNumberOfPeople > numberOfPeople) {
            throw new RuntimeException("현재 인원이 더 많기 때문에 스터디 인원을 변경할 수 없습니다.");
        }

        this.online = online;
        this.offline = offline;
        if (offline) {
        }

        if (open) {
            this.status = StudyStatus.OPEN;
        } else {
            this.status = StudyStatus.CLOSE;
        }
        this.category = category;
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changeTags(List<String> requestTags) {
        List<Tag> tags = requestTags.stream()
                .map(content -> Tag.createTag(content, this))
                .collect(Collectors.toList());

        this.tags.clear();
        this.tags.addAll(tags);
    }

    public void addTag(String content) {
        boolean tagResult = tags.stream()
                .anyMatch(tag -> tag.getContent().equals(content));
        if (tagResult) {
            throw new RuntimeException(content + "는 이미 존재하는 태그입니다.");
        }

        Tag tag = Tag.createTag(content, this);
        tags.add(tag);
    }

    public void deleteTag(Long tagId) {
        Tag findTag = tags.stream()
                .filter(tag -> tag.getId().equals(tagId))
                .findFirst().orElseThrow(() -> new RuntimeException(tagId + "는 존재하지 않는 태그 ID 입니다."));
        tags.remove(findTag);
    }

}
