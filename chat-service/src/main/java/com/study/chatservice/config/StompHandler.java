package com.study.chatservice.config;

import com.study.chatservice.config.security.JwtTokenProvider;
import com.study.chatservice.repository.ChatRoomInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

/**
 * 클라이언트의 입장/퇴장 이벤트를 서버에서 체크하여 메세지를 전송
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomInfoRepository chatRoomInfoRepository;
    
    /**
     * 웹 소켓을 통해 들어온 요청이 처리되기 전 실행
     * 채팅방 입장 시 이벤트 : StompCommand.SUBSCRIBE
     * 채팅방 퇴장 시 이벤트 : StompCommand.DISCONNECT
     * 채팅방 입장/퇴장 시 채팅룸의 인원수 +- 처리
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 웹 소켓 연길 시 헤더의 jwt Token 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {

            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("CONNECT {}", jwtToken);

            // Header의 jwt Token 검증
            jwtTokenProvider.validateToken(jwtToken);

        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅방 구독 요청

            String jwtToken = accessor.getFirstNativeHeader("token");
            Long userId = jwtTokenProvider.getUserIdFromJwt(jwtToken);

            // header 정보에서 구독 destination 정보를 얻고 , roomId를 추출
            String roomId = getRoomId(Optional.ofNullable((String) message
                    .getHeaders()
                    .get("simpDestination"))
                    .orElse("InvalidRoomId"));

            // 채팅방에 들어온 클라이언트 sessionId를 roomId와 매핑해놓는다.
            // 나중에 특정 세션이 어떤 채팅방에 들어가 있는지 알기 위함
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            chatRoomInfoRepository.setUserEnterInfo(sessionId, roomId);
            chatRoomInfoRepository.addChatRoomUser(roomId,sessionId,userId);

            log.info("SUBSCRIBED {},{}", userId, roomId);

        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // 웹 소켓 연결 종료

            // 연결이 종료된 클라이언트 sessionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            Long roomId = chatRoomInfoRepository.getUserEnterRoomId(sessionId);

            chatRoomInfoRepository.removeChatRoomUser(String.valueOf(roomId),sessionId);

            // 퇴장한 클라이언트의 roomId 맵핑정보를 삭제한다.
            chatRoomInfoRepository.removeUserEnterInfo(sessionId);
            log.info("DISCONNECTED {},{}",sessionId,roomId);
        }
        return message;
    }

    /**
     * destination 정보에서 roomId 추출
     */
    public String getRoomId(String destination){
        int lastIndex = destination.lastIndexOf("/");
        if(lastIndex != -1){
            return destination.substring(lastIndex + 1);
        }
        else
            return "";
    }
}
