package com.example.websockettest_backend.chat;

import com.example.websockettest_backend.chat.dto.ChatMessage;
import com.example.websockettest_backend.ChatMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat")
    @SendTo("/topic/chat/room/{roomId}")  // 동적 라우팅 추가
    public ChatMessage message(ChatMessage chatMessage) {
        // 기존 코드 동일
        chatMessage.setTimestamp(System.currentTimeMillis());

        switch (chatMessage.getType()) {
            case ENTER:
                chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");
                break;
            case LEAVE:
                chatMessage.setMessage(chatMessage.getSender() + "님이 퇴장했습니다.");
                break;
            case TALK:
                break;
        }

        chatMessageRepository.saveMessage(chatMessage);

        // 특정 방에 메시지 전송
        messagingTemplate.convertAndSend("/topic/chat/room/" + chatMessage.getRoomId(), chatMessage);

        return chatMessage;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Exception e) {
        logger.error("WebSocket message handling error", e);
        return "An error occurred: " + e.getMessage();
    }
}