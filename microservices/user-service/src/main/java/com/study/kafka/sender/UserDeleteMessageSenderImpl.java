package com.study.kafka.sender;

import com.study.kakfa.UserDeleteMessage;
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
public class UserDeleteMessageSenderImpl implements UserDeleteMessageSender {

    @Qualifier("userDeleteKafkaTemplate")
    private final KafkaTemplate<String, UserDeleteMessage> kafkaTemplate;

    @Value("${kafka.topic.user.delete}")
    private String topic;

    @Override
    public void send(UserDeleteMessage userDeleteMessage) {
        Message<UserDeleteMessage> message = MessageBuilder
                .withPayload(userDeleteMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, UserDeleteMessage>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, UserDeleteMessage>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("Unable to send Message due to : {}", ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, UserDeleteMessage> result) {
                log.info("Send Message={} to topic {} with offset = {}",
                        result.getProducerRecord().value().toString(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().hasOffset());
            }
        });
    }
}
