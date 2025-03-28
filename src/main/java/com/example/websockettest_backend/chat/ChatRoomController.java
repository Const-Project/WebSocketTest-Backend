package com.example.websockettest_backend.chat;

import com.example.websockettest_backend.chat.dto.ChatRoomDto;
import com.example.websockettest_backend.chat.dto.ChatMessage;
import com.example.websockettest_backend.chat.ChatRoomRepository;
import com.example.websockettest_backend.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅방 생성
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDto> createRoom(@RequestParam String roomName) {
        ChatRoomDto room = chatRoomRepository.createRoom(roomName);
        return ResponseEntity.ok(room);
    }

    // 모든 채팅방 조회
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getAllRooms() {
        return ResponseEntity.ok(chatRoomRepository.findAllRooms());
    }

    // 특정 채팅방 조회
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<ChatRoomDto> getRoomDetails(@PathVariable String roomId) {
        ChatRoomDto room = chatRoomRepository.findRoomById(roomId);
        if (room == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(room);
    }

    // 채팅방 메시지 히스토리 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getRoomMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int limit
    ) {
        List<ChatMessage> messages = chatMessageRepository.findRecentMessages(roomId, limit);
        return ResponseEntity.ok(messages);
    }
}
