package com.study.kafka.sender;

import com.netflix.discovery.converters.Auto;
import com.study.kakfa.MessageCreateMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class MessageCreateSenderTest {

    @Autowired
    private MessageCreateSender messageCreateSender;

    @Test
    @DisplayName("채팅 생성 메세지를 전송한다.")
    void send() {
        messageCreateSender.send(MessageCreateMessage.from(1L, "황주환", "안녕하세요", Arrays.asList(1L,2L)));
    }
}