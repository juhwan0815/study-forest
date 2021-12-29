package com.study.repository;

import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Getter
public class ChatRoomRepository {

    private Map<Long, Map<String, Long>> chatRoomUsers = new ConcurrentHashMap<>();
    private Map<String, Long> sessionRooms = new ConcurrentHashMap<>();

    public void addChatRoomUser(Long roomId, String sessionId, Long userId) {
        Map<String, Long> users = chatRoomUsers.get(roomId);
        if(users == null){
            users = new ConcurrentHashMap<>();
            chatRoomUsers.put(roomId, users);
        }
        users.put(sessionId, userId);
    }

    public void deleteChatRoomUser(Long roomId, String sessionId) {
        Map<String, Long> users = chatRoomUsers.get(roomId);
        users.remove(sessionId);
    }

    public void deleteChatRoom(List<Long> roomIds){
        roomIds.forEach(roomId -> chatRoomUsers.remove(roomId));
    }

    public void addSessionRoom(String sessionId, Long roomId) {
        sessionRooms.put(sessionId, roomId);
    }

    public void deleteSessionRoom(String sessionId) {
        sessionRooms.remove(sessionId);
    }

    public Long getRoomBySession(String sessionId) {
        return sessionRooms.get(sessionId);
    }

    public List<Long> getChatRoomUsers(Long roomId){
        Map<String, Long> users = chatRoomUsers.get(roomId);
        return new ArrayList<>(users.values());
    }

}
