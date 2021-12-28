package com.study.kakfa.sender;

import com.study.kakfa.StudyApplySuccessMessage;
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
public class StudyApplySuccessMessageSenderImpl implements StudyApplySuccessMessageSender {

    @Qualifier("studyApplySuccessKafkaTemplate")
    private final KafkaTemplate<String, StudyApplySuccessMessage> kafkaTemplate;

    @Value("${kafka.topic.studyApply.success}")
    private String topic;

    @Override
    public void send(StudyApplySuccessMessage studyApplySuccessMessage) {

        Message<StudyApplySuccessMessage> message = MessageBuilder
                .withPayload(studyApplySuccessMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        kafkaTemplate.send(message);
    }
}
