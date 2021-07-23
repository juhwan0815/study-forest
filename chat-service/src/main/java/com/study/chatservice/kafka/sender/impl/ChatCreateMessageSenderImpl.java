package com.study.chatservice.kafka.sender.impl;


import com.study.chatservice.kafka.message.ChatCreateMessage;
import com.study.chatservice.kafka.message.StudyDeleteMessage;
import com.study.chatservice.kafka.sender.ChatCreateMessageSender;
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
public class ChatCreateMessageSenderImpl implements ChatCreateMessageSender {

    @Qualifier("chatCreateKafkaTemplate")
    private final KafkaTemplate<String, ChatCreateMessage> kafkaTemplate;

    @Value("${kafka.topic.chat.create}")
    private String topic;

    @Override
    public void send(ChatCreateMessage chatCreateMessage) {
        Message<ChatCreateMessage> message = MessageBuilder
                .withPayload(chatCreateMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, ChatCreateMessage>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, ChatCreateMessage>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Unable to send Message due to : {}",ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, ChatCreateMessage> result) {
                log.info("Send Message={} to topic {} with offset = {}",
                        result.getProducerRecord().value().toString(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().hasOffset());
            }
        });
    }
}
