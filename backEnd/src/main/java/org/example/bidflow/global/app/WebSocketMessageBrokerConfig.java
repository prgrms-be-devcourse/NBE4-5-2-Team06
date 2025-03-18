package org.example.bidflow.global.app;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker       // STOMP 사용 명시
@RequiredArgsConstructor

public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandshakeHandler stompHandshakeHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/sub"); // 구독(Subscribe) 경로 (서버 -> 클라이언트 로 메시지 보낼 때)
        config.setApplicationDestinationPrefixes("/app"); // 메시지 보낼 prefix (클라이언트 -> 서버)
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 연결할 엔드포인트
        registry.addEndpoint("/ws")         // -> ws://localhost:8080/ws
                .setAllowedOrigins("http://35.203.149.35:3000") // CORS 허용 (모든 도메인 허용)
                .addInterceptors(stompHandshakeHandler) // HandshakeInterceptor 추가 (JWT 검증)
                .withSockJS();  // socket fallback 지원
    }
}
