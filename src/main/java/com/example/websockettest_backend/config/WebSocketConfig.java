package com.example.websockettest_backend.config;

import com.example.websockettest_backend.security.JwtUtil;
import com.example.websockettest_backend.user.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // Support for topic and user-specific queues
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*") // 모든 origin 허용
                .withSockJS()
                .setSuppressCors(true)
                .setSessionCookieNeeded(false)
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js");// Fallback options for browsers not supporting WebSocket
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                        message, StompHeaderAccessor.class
                );
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    logger.info("Processing CONNECT command");
                logger.info("Received WebSocket message: {}", message);

                // Extract authentication token from headers
                String authToken = extractAuthToken(message);
                logger.info("Extracted auth token: {}", authToken != null ? "Present" : "Null");

                if (authToken != null) {
                    try {
                        // Validate and set authentication
                        String username = jwtUtil.extractUsername(authToken);
                        logger.info("Extracted username: {}", username);

                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        logger.info("User details found for: {}", username);

                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.info("Authentication set successfully for user: {}", username);
                    } catch (Exception e) {
                        // Log authentication failure with details
                        logger.error("Authentication failed: ", e);
                        SecurityContextHolder.clearContext();
                    }
                }
                }
                return message;
            }
        });
    }

    private String extractAuthToken(Message<?> message) {
        try {
            Object headers = message.getHeaders().get("nativeHeaders");
            if (headers instanceof Map) {
                Map<String, Object> nativeHeaders = (Map<String, Object>) headers;
                List<String> authHeaders = (List<String>) nativeHeaders.get("Authorization");

                // Detailed logging for header extraction
                if (authHeaders != null) {
                    System.out.println("Authorization headers: " + authHeaders);
                    for (String header : authHeaders) {
                        System.out.println("Individual header: " + header);
                    }
                } else {
                    System.out.println("No Authorization headers found");
                }

                if (authHeaders != null && !authHeaders.isEmpty()) {
                    String authHeader = authHeaders.get(0);
                    if (authHeader.startsWith("Bearer ")) {
                        return authHeader.substring(7);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in extractAuthToken: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}