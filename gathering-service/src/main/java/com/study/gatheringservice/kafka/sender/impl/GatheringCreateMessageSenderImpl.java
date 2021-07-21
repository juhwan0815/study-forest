package com.study.gatheringservice.kafka.sender.impl;

import com.study.gatheringservice.kafka.message.GatheringCreateMessage;
import com.study.gatheringservice.kafka.sender.GatheringCreateMessageSender;
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
public class GatheringCreateMessageSenderImpl implements GatheringCreateMessageSender {

    @Qualifier("gatheringCreateKafkaTemplate")
    private final KafkaTemplate<String, GatheringCreateMessage> kafkaTemplate;

    @Value("${kafka.topic.gathering.create}")
    private String topic;

    @Override
    public void send(GatheringCreateMessage gatheringCreateMessage) {
        Message<GatheringCreateMessage> message = MessageBuilder
                .withPayload(gatheringCreateMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, GatheringCreateMessage>> future = kafkaTemplate.send(message);

        future.addCallback(new ListenableFutureCallback<SendResult<String, GatheringCreateMessage>>() {
            @Override
            public void onSuccess(SendResult<String, GatheringCreateMessage> result) {
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
