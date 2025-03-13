package org.example.bidflow.global.messaging.subscriber;

import org.example.bidflow.global.messaging.dto.MessagePayload;

public interface MessageSubscriber<T> {
    void subscribe(String topic);
    void onMessage(String topic,T payload);

}