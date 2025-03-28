package com.example.websockettest_backend.chat;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatRoomService {
    private Map<String, Set<String>> chatRooms = new ConcurrentHashMap<>();

    public void createRoom(String roomId) {
        chatRooms.putIfAbsent(roomId, ConcurrentHashMap.newKeySet());
    }

    public void enterRoom(String roomId, String username) {
        chatRooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(username);
    }

    public void leaveRoom(String roomId, String username) {
        if (chatRooms.containsKey(roomId)) {
            chatRooms.get(roomId).remove(username);
        }
    }

    public Set<String> getUsersInRoom(String roomId) {
        return chatRooms.getOrDefault(roomId, Collections.emptySet());
    }
}