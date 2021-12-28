package com.study.kakfa.sender;

import com.study.kakfa.GatheringCreateMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;


@SpringBootTest
@DirtiesContext // 컨텍스트가 다른 테스트 간에 정리되고 재설정
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}) // 내장 카프카를 테스트에 삽입
class GatheringCreateMessageSenderTest {

    @Autowired
    private GatheringCreateMessageSender gatheringCreateMessageSender;

    @Test
    void send() {
        gatheringCreateMessageSender.send(GatheringCreateMessage.from(1L, LocalDateTime.now(), true, "모임"));
    }
}