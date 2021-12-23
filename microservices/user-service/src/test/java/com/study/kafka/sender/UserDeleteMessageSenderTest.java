package com.study.kafka.sender;

import com.study.kakfa.UserDeleteMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext // 컨텍스트가 다른 테스트 간에 정리되고 재설정
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" }) // 내장 카프카를 테스트에 삽입
class UserDeleteMessageSenderTest {

    @Autowired
    private UserDeleteMessageSender userDeleteMessageSender;

    @Test
    @DisplayName("회원 삭제 메세지를 보낸다")
    public void send(){
        userDeleteMessageSender.send(new UserDeleteMessage(1L));
    }
}