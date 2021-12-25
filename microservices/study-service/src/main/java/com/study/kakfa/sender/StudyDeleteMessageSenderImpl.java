package com.study.kakfa.sender;


import com.study.kakfa.StudyDeleteMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class StudyDeleteMessageSenderImpl implements StudyDeleteMessageSender {

    @Qualifier("studyDeleteKafkaTemplate")
    private final KafkaTemplate<String, StudyDeleteMessage> kafkaTemplate;

    @Value("${kafka.topic.study.delete}")
    private String topic;

    @Override
    public void send(StudyDeleteMessage studyDeleteMessage) {
        Message<StudyDeleteMessage> message = MessageBuilder
                .withPayload(studyDeleteMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, StudyDeleteMessage>> future = kafkaTemplate.send(message);
    }
}
