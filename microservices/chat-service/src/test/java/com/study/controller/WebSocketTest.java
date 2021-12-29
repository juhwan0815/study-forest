package com.study.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.dto.MessageRequest;
import com.study.dto.MessageResponse;
import com.study.service.MessageService;
import com.study.utils.jwt.JwtUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import sun.net.ConnectionResetException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;

import static com.study.MessageFixture.TEST_MESSAGE_RESPONSE;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketTest {

    private final String TEST_AUTHORIZATION = "Bearer *****";

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private MessageService messageService;

    private WebSocketStompClient webSocketStompClient;

    private StompSession session;

    private BlockingQueue<String> messageResponses;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
        List<Transport> transports = singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);
        webSocketStompClient = new WebSocketStompClient(sockJsClient);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        messageResponses = new LinkedBlockingDeque<>();
    }

    @Test
    @DisplayName("채팅 메세지를 전송한다.")
    void sendMessage() throws Exception {
        // given
        given(jwtUtils.validate(any()))
                .willReturn(true);

        given(jwtUtils.getUserId(any()))
                .willReturn(1L);

        given(jwtUtils.getNickName(any()))
                .willReturn("황주환");


        given(messageService.sendMessage(any(), any(), any()))
                .willReturn(TEST_MESSAGE_RESPONSE);

        // when
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION);
        session = webSocketStompClient.connect("ws://localhost:" + port + "/ws-stomp", null, stompHeaders, new StompSessionHandlerAdapter() {
        }, new Object[0]).get(60, SECONDS);

        stompHeaders = new StompHeaders();
        stompHeaders.add(StompHeaders.DESTINATION, String.format("/sub/chat/room/%s", 1L));
        stompHeaders.add(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION);
        session.subscribe(stompHeaders, new StompFrameHandlerImpl<>(messageResponses));

        stompHeaders = new StompHeaders();
        stompHeaders.add(StompHeaders.DESTINATION, String.format("/pub/chat/message"));
        stompHeaders.add(HttpHeaders.AUTHORIZATION, TEST_AUTHORIZATION);
        session.send(stompHeaders, new MessageRequest(1L, "안녕하세요"));

        String content = messageResponses.poll(5, SECONDS);

        MessageResponse result = objectMapper.readValue(content, MessageResponse.class);

        // then
        assertThat(result.getUserId()).isEqualTo(1);
        assertThat(result.getSender()).isEqualTo("황주환");
        assertThat(result.getContent()).isEqualTo("안녕하세요");
        assertThat(result.getCreatedAt()).isNotNull();
        then(jwtUtils).should(times(1)).validate(any());
        then(jwtUtils).should(times(2)).getUserId(any());
        then(jwtUtils).should(times(1)).getNickName(any());
        then(messageService).should(times(1)).sendMessage(any(),any(),any());
    }

    @Test
    @DisplayName("예외 테스트: 비회원이 채팅 메세지를 전송하면 예외가 발생한다.")
    void sendMessageNotUser() throws Exception {
        // when
        assertThrows(ExecutionException.class,
                ()->webSocketStompClient.connect("ws://localhost:" + port + "/ws-stomp", null, null, new StompSessionHandlerAdapter() {}, new Object[0]).get(60, SECONDS));
    }

    static class StompFrameHandlerImpl<T> implements StompFrameHandler {

        private ObjectMapper objectMapper;
        private BlockingQueue<String> messageResponses;

        public StompFrameHandlerImpl(BlockingQueue<String> messageResponses) {
            this.objectMapper = new ObjectMapper();
            this.messageResponses = messageResponses;
        }

        // payload 를 받을 클래스 타입을 지정
        @Override
        public Type getPayloadType(StompHeaders headers) {
            return byte[].class;
        }

        // payload 가 담길 BlockingQueue 지정
        @SneakyThrows
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            String content = new String((byte[]) payload);
            messageResponses.offer(content);
        }
    }
}