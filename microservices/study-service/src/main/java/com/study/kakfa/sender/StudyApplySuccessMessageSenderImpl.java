package com.study.kakfa.sender;

import com.study.kakfa.StudyApplySuccessMessage;
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

@Component
@RequiredArgsConstructor
public class StudyApplySuccessMessageSenderImpl implements StudyApplySuccessMessageSender {

    @Qualifier("studyApplySuccessKafkaTemplate")
    private final KafkaTemplate<String, StudyApplySuccessMessage> kafkaTemplate;

    @Value("${kafka.topic.studyApply.success}")
    private String topic;

    @Override
    public void send(StudyApplySuccessMessage studyApplySuccessMessage) {

        Message<StudyApplySuccessMessage> message = MessageBuilder
                .withPayload(studyApplySuccessMessage)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();

        ListenableFuture<SendResult<String, StudyApplySuccessMessage>> future = kafkaTemplate.send(message);
    }
}
