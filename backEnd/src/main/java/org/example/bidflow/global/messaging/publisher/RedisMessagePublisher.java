package org.example.bidflow.global.messaging.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.global.messaging.dto.MessagePayload;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis Pub/Sub 기반 구현체
 */

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher<MessagePayload> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(String topic, MessagePayload payload) {
        String json = convertToJson(payload);
        redisTemplate.convertAndSend(topic, payload);
    }

    // 직렬화
    private String convertToJson(MessagePayload messagePayload) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        return objectMapper.convertValue(messagePayload, String.class);
        return messagePayload.toString();
    }
}