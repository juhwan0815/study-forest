package com.study.studyservice.kafka.sender.impl;

import com.study.studyservice.kafka.message.StudyDeleteMessage;
import com.study.studyservice.kafka.message.StudyJoinMessage;
import com.study.studyservice.kafka.sender.KafkaStudyJoinMessageSender;
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
public class KafkaStudyJoinMessageSenderImpl implements KafkaStudyJoinMessageSender {

    @Qualifier("studyJoinKafkaTemplate")
    private final KafkaTemplate<String, StudyJoinMessage> kafkaTemplate;

    @Value("${kafka.topic.study.join.name}")
    private String topic;

    @Override
    public void send(StudyJoinMessage studyJoinMessage) {
        Message<StudyJoinMessage> message = MessageBuilder
                .withPayload(studyJoinMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, StudyJoinMessage>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, StudyJoinMessage>>() {
            @Override
            public void onSuccess(SendResult<String, StudyJoinMessage> result) {
                log.info("Send Message={} to topic {} with offset = {}",
                        result.getProducerRecord().value().toString(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().hasOffset());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.info("Unable to send Message due to : {}", ex.getMessage());
            }
        });

    }
}
