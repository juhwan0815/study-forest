package com.study.kakfa.sender;

import com.study.kakfa.StudyApplySuccessMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext // 컨텍스트가 다른 테스트 간에 정리되고 재설정
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}) // 내장 카프카를 테스트에 삽입
class StudyApplySuccessMessageSenderTest {

    @Autowired
    private StudyApplySuccessMessageSender studyApplySuccessMessageSender;

    @Test
    @DisplayName("스터디 침기 상공 메세지를 보낸다.")
    void send() {
        studyApplySuccessMessageSender.send(StudyApplySuccessMessage.from(1L, 1L, "스프링"));
    }

}