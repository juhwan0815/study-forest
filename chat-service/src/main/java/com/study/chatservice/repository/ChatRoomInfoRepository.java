package com.study.chatservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 채팅방을 생성하고 정보를 조회하는 리포지토리
 */
@Repository
@RequiredArgsConstructor
public class ChatRoomInfoRepository {

    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    private static final String CHAT_ROOM_USER = "CHAT_ROOM_USER"; // 채팅룸 저장
    private static final String USER_COUNT ="USER_COUNT"; // 채팅룸에 입장한 클라이언트 수 저장
    private static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 매핑한 정보 저장

    @Resource(name = "redisTemplate")
    private HashOperations<String,String,Long> hashOpsEnterInfo;

    @Resource(name = "redisTemplate")
    private HashOperations<String,String,Long> hashOpsChatRoomUser;

    // 유저가 입장한 채팅방 Id와 유저 세션 ID 매핑 정보 저장
    public void setUserEnterInfo(String sessionId,String roomId){
        hashOpsEnterInfo.put(ENTER_INFO,sessionId,Long.valueOf(roomId));
    }

    public void addChatRoomUser(String roomId,String sessionId,Long userId){
        hashOpsChatRoomUser.put(roomId,sessionId,userId);
    }

    public void removeChatRoomUser(String roomId,String sessionId){
        hashOpsChatRoomUser.delete(roomId,sessionId);
    }

    public List<Long> getChatRoomUser(String roomId){
        Map<String, Long> chatRoomUsers = hashOpsChatRoomUser.entries(roomId);
        return new ArrayList<>(chatRoomUsers.values());
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public Long getUserEnterRoomId(String sessionId){
        return hashOpsEnterInfo.get(ENTER_INFO,sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방 ID 삭제
    public void removeUserEnterInfo(String sessionId){
        hashOpsEnterInfo.delete(ENTER_INFO,sessionId);
    }
}
