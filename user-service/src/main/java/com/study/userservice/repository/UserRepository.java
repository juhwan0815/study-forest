package com.study.userservice.repository;

import com.study.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByKakaoId(Long kakaoId);

    Optional<User> findByNickName(String nickName);
}
