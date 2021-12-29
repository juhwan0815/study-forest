package com.study.config;

import com.study.repository.ChatRoomRepository;
import com.study.utils.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 클라이언트의 입장/퇴장 이벤트를 서버에서 체크하여 메세지를 전송
 */
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtUtils jwtUtils;
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 웹 소켓을 통해 들어온 요청이 처리되기 전 실행
     * 채팅방 입장 시 이벤트 : StompCommand.SUBSCRIBE
     * 채팅방 퇴장 시 이벤트 : StompCommand.DISCONNECT
     * 채팅방 입장/퇴장 시 채팅룸의 인원수 +- 처리
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT == accessor.getCommand()) {
            String bearerToken = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

            if (bearerToken == null) {
                throw new RuntimeException("");
            }
            jwtUtils.validate(bearerToken.substring(7));

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅방 구독 요청

            String bearerToken = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
            Long userId = jwtUtils.getUserId(bearerToken.substring(7));

            String roomId = getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            String sessionId = (String) message.getHeaders().get("simpSessionId");

            chatRoomRepository.addChatRoomUser(Long.valueOf(roomId), sessionId, userId);
            chatRoomRepository.addSessionRoom(sessionId, Long.valueOf(roomId));
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // 웹 소켓 연결 종료

            String sessionId = (String) message.getHeaders().get("simpSessionId");
            Long roomId = chatRoomRepository.getRoomBySession(sessionId);

            chatRoomRepository.deleteChatRoomUser(roomId, sessionId);
            chatRoomRepository.deleteSessionRoom(sessionId);
        }

        return message;
    }

    /**
     * destination 정보에서 roomId 추출
     */
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf("/");
        return destination.substring(lastIndex + 1);
    }
}
