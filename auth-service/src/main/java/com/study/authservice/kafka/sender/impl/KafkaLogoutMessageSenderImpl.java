package com.study.authservice.kafka.sender.impl;

import com.study.authservice.kafka.message.LogoutMessage;
import com.study.authservice.kafka.sender.KafkaLogoutMessageSender;
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
public class KafkaLogoutMessageSenderImpl implements KafkaLogoutMessageSender {

    @Qualifier("logoutKafkaTemplate")
    private final KafkaTemplate<String, LogoutMessage> kafkaTemplate;

    @Value("${kafka.topic.logout.name}")
    private String topic;

    @Override
    public void send(LogoutMessage logoutMessage) {

        Message<LogoutMessage> message = MessageBuilder
                .withPayload(logoutMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, LogoutMessage>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, LogoutMessage>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Unable to send Message due to : {}",ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, LogoutMessage> result) {
                log.info("Send Message={} to topic {} with offset = {}",
                        result.getProducerRecord().value().toString(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().hasOffset());
            }
        });
    }
}
