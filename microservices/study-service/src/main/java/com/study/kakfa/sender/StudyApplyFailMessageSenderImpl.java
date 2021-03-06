package com.study.kakfa.sender;

import com.study.kakfa.StudyApplyFailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudyApplyFailMessageSenderImpl implements StudyApplyFailMessageSender {


    @Qualifier("studyApplyFailKafkaTemplate")
    private final KafkaTemplate<String, StudyApplyFailMessage> kafkaTemplate;

    @Value("${kafka.topic.studyApply.fail}")
    private String topic;

    @Override
    public void send(StudyApplyFailMessage studyApplyFailMessage) {
        Message<StudyApplyFailMessage> message = MessageBuilder
                .withPayload(studyApplyFailMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(message);
    }
}
