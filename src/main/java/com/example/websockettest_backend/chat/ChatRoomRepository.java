package com.example.websockettest_backend.chat;

import com.example.websockettest_backend.chat.dto.ChatRoomDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ChatRoomRepository {
    private Map<String, ChatRoomDto> chatRooms = new LinkedHashMap<>();

    public ChatRoomDto createRoom(String roomName) {
        String roomId = UUID.randomUUID().toString();
        ChatRoomDto room = ChatRoomDto.builder()
                .roomId(roomId)
                .roomName(roomName)
                .participantCount(0)
                .build();

        chatRooms.put(roomId, room);
        return room;
    }

    public List<ChatRoomDto> findAllRooms() {
        return new ArrayList<>(chatRooms.values());
    }

    public ChatRoomDto findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }
}