package com.study.authservice.kafka.sender.impl;

import com.study.authservice.kafka.message.RefreshTokenCreateMessage;
import com.study.authservice.kafka.sender.KafkaRefreshTokenCreateMessageSender;
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
public class KafkaRefreshTokenCreateMessageSenderImpl implements KafkaRefreshTokenCreateMessageSender {

    @Qualifier("refreshTokenCreateKafkaTemplate")
    private final KafkaTemplate<String,RefreshTokenCreateMessage> kafkaTemplate;

    @Value("${kafka.topic.refresh.name}")
    private String topic;

    @Override
    public void send(RefreshTokenCreateMessage refreshTokenCreateMessage) {

        Message<RefreshTokenCreateMessage> message = MessageBuilder
                .withPayload(refreshTokenCreateMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, RefreshTokenCreateMessage>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, RefreshTokenCreateMessage>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Unable to send Message due to : {}",ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, RefreshTokenCreateMessage> result) {
                log.info("Send Message={} to topic {} with offset = {}",
                        result.getProducerRecord().value().toString(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().hasOffset());
            }
        });

    }
}
