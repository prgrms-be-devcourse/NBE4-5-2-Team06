package org.example.bidflow.global.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessagePayload {
    private String eventType;  // 이벤트 타입 (예: AUCTION_BID, USER_SIGNUP 등)
    private String sender;      // 발신자 정보
    private Object data;       // JSON 형태 데이터
}
