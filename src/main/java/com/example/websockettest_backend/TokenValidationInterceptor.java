package com.example.websockettest_backend;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class TokenValidationInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // Extract and validate token from connection request
            String token = accessor.getFirstNativeHeader("Authorization");

            if (!validateToken(token)) {
                throw new IllegalArgumentException("Invalid or missing token");
            }
        }

        return message;
    }

    private boolean validateToken(String token) {
        // Implement your token validation logic here
        // This could involve checking with an authentication service
        // Verify token signature, expiration, etc.
        return token != null && !token.isEmpty();
    }
}
