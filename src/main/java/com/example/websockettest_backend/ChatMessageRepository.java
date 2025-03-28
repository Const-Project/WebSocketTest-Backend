package com.example.websockettest_backend;

import com.example.websockettest_backend.chat.dto.ChatMessage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ChatMessageRepository {
    private Map<String, List<ChatMessage>> roomMessages = new HashMap<>();

    public void saveMessage(ChatMessage message) {
        roomMessages.computeIfAbsent(message.getRoomId(), k -> new ArrayList<>())
                .add(message);
    }


    public List<ChatMessage> findRecentMessages(String roomId, int limit) {
        List<ChatMessage> messages = roomMessages.getOrDefault(roomId, new ArrayList<>());
        return messages.stream()
                .sorted((m1, m2) -> Long.compare(m2.getTimestamp(), m1.getTimestamp()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
