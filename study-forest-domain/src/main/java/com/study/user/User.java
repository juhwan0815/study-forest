package com.study.user;

import com.study.area.Area;
import com.study.common.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id; // 회원 ID

    private Long kakaoId; // 카카오 ID

    private String nickName; // 닉네임

    private String imageUrl; // 이미지 URL

    private String ageRange; // 나이대

    private String gender; // 성별

    @Enumerated(EnumType.STRING)
    private Role role; // 권한

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    private Integer distance; // 검색 거리

    private String pushToken; // fcmToken

    public static User createUser(Long kakaoId, String nickName, String ageRange, String gender, Role role) {
        User user = new User();
        user.kakaoId = kakaoId;
        user.nickName = nickName;
        user.ageRange = ageRange;
        user.gender = gender;
        user.role = role;
        user.distance = 3;
        return user;
    }

    public void changProfile(String nickName, String imageUrl) {
        this.nickName = nickName;
        this.imageUrl = imageUrl;
    }

    public void changeDistance(Integer distance) {
        this.distance = distance;
    }

    public void changeImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void changePushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public void changeArea(Area area) {
        this.area = area;
    }
}
