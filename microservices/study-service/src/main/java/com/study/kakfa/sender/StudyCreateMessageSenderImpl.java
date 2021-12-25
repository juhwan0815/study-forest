package com.study.kakfa.sender;

import com.study.kakfa.StudyCreateMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;


@Component
@RequiredArgsConstructor
public class StudyCreateMessageSenderImpl implements StudyCreateMessageSender {

    @Qualifier("studyCreateKafkaTemplate")
    private final KafkaTemplate<String, StudyCreateMessage> kafkaTemplate;

    @Value("${kafka.topic.study.create}")
    private String topic;

    @Override
    public void send(StudyCreateMessage studyCreateMessage) {

        Message<StudyCreateMessage> message = MessageBuilder
                .withPayload(studyCreateMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, StudyCreateMessage>> future = kafkaTemplate.send(message);
    }
}