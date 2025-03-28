package com.example.websockettest_backend.security;

import com.example.websockettest_backend.user.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

@Configuration
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() == StompCommand.CONNECT) {
            String authToken = accessor.getFirstNativeHeader("Authorization");

            if (authToken != null && authToken.startsWith("Bearer ")) {
                String token = authToken.substring(7);
                try {
                    // JWT 토큰 검증 로직
                    String username = jwtUtil.extractUsername(token);
                    UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
                    if (username != null && jwtUtil.validateToken(token,userDetails)) {
                        // 인증 성공
                        return message;
                    }
                } catch (Exception e) {
                    // 토큰 검증 실패
                    throw new AuthenticationCredentialsNotFoundException("Invalid token");
                }
            }

            throw new AuthenticationCredentialsNotFoundException("No token provided");
        }

        return message;
    }
}
