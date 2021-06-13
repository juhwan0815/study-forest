package com.study.studyservice.kafka.sender.impl;


import com.study.studyservice.kafka.message.StudyDeleteMessage;
import com.study.studyservice.kafka.sender.KafkaStudyDeleteMessageSender;
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
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaStudyDeleteMessageSenderImpl implements KafkaStudyDeleteMessageSender {

    @Qualifier("studyDeleteKafkaTemplate")
    private final KafkaTemplate<String,StudyDeleteMessage> kafkaTemplate;

    @Value("${kafka.topic.study.delete.name}")
    private String topic;

    @Override
    public void send(StudyDeleteMessage studyDeleteMessage) {
        Message<StudyDeleteMessage> message = MessageBuilder
                .withPayload(studyDeleteMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, StudyDeleteMessage>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, StudyDeleteMessage>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Unable to send Message due to : {}",ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, StudyDeleteMessage> result) {
                log.info("Send Message={} to topic {} with offset = {}",
                        result.getProducerRecord().value().toString(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().hasOffset());
            }
        });
    }
}
