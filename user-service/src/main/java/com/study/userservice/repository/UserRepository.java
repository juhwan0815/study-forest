package com.study.userservice.repository;

import com.study.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByKakaoId(Long kakaoId);

    Optional<User> findByNickName(String nickName);

    @Query("select u from User u left join fetch u.studyJoins where u.id =:userId ")
    Optional<User> findWithStudyJoinById(@Param("userId") Long userId);
}
