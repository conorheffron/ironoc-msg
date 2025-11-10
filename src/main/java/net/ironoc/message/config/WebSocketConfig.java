package net.ironoc.message.config;

import net.ironoc.message.security.CustomHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // Message destinations
        config.setApplicationDestinationPrefixes("/app"); // Prefix for client messages
        config.setUserDestinationPrefix("/user"); // Prefix for user-specific messages
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ironoc-ws") // WebSocket endpoint
                .setAllowedOriginPatterns("*") // Allow all origins
                .addInterceptors(new CustomHandshakeInterceptor()); // Add the interceptor
//                .withSockJS(); // Enable SockJS fallback
    }
}
