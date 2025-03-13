package org.example.bidflow.global.messaging.listener;

import org.example.bidflow.global.messaging.dto.MessagePayload;

/**
 * 실제 메시지를 받아 로직 처리(핸들링)라는 Listener
 * Subscribe의 onMessage 호출되거나,
 * 혹은 @KafkaListener 등을 통해 직접 로직 처리 가능할 듯!
 */

public interface MessageListener {
    void handleMessage(MessagePayload payload);
}
