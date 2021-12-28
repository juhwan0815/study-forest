package com.study.kakfa.sender;

import com.study.kakfa.ChatRoomDeleteMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext // 컨텍스트가 다른 테스트 간에 정리되고 재설정
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}) // 내장 카프카를 테스트에 삽입
class ChatRoomDeleteMessageSenderTest {

    @Autowired
    private ChatRoomDeleteMessageSender chatRoomDeleteMessageSender;

    @Test
    void send() {
        chatRoomDeleteMessageSender.send(ChatRoomDeleteMessage.from(1L));
    }
}