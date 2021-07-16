package com.study.studyservice.kafka.sender.impl;

import com.study.studyservice.kafka.message.StudyApplyCancelMessage;
import com.study.studyservice.kafka.message.StudyApplyCreateMessage;
import com.study.studyservice.kafka.sender.StudyApplyCancelMessageSender;
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
public class StudyApplyCancelMessageSenderImpl implements StudyApplyCancelMessageSender {


    @Qualifier("studyApplyCancelKafkaTemplate")
    private final KafkaTemplate<String, StudyApplyCancelMessage> kafkaTemplate;

    @Value("${kafka.topic.studyApply.cancel}")
    private String topic;

    @Override
    public void send(StudyApplyCancelMessage studyApplyCancelMessage) {
        Message<StudyApplyCancelMessage> message = MessageBuilder
                .withPayload(studyApplyCancelMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, StudyApplyCancelMessage>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, StudyApplyCancelMessage>>() {
            @Override
            public void onSuccess(SendResult<String, StudyApplyCancelMessage> result) {
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
