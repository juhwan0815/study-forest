package com.study.userservice.kafka.sender.impl;

import com.study.userservice.kafka.message.UserDeleteMessage;
import com.study.userservice.kafka.message.UserUpdateProfileMessage;
import com.study.userservice.kafka.sender.UserUpdateProfileMessageSender;
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
public class UserUpdateProfileMessageSenderImpl implements UserUpdateProfileMessageSender {

    @Qualifier("userUpdateProfileKafkaTemplate")
    private final KafkaTemplate<String, UserUpdateProfileMessage> kafkaTemplate;

    @Value("${kafka.topic.user.update}")
    private String topic;

    @Override
    public void send(UserUpdateProfileMessage userUpdateProfileMessage) {
        Message<UserUpdateProfileMessage> message = MessageBuilder
                .withPayload(userUpdateProfileMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, UserUpdateProfileMessage>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, UserUpdateProfileMessage>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Unable to send Message due to : {}",ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, UserUpdateProfileMessage> result) {
                log.info("Send Message={} to topic {} with offset = {}",
                        result.getProducerRecord().value().toString(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().hasOffset());
            }
        });
    }
}
