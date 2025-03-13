package org.example.bidflow.domain.bid.dto.model.response.webSocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Builder
@RequiredArgsConstructor
public class WebSocketResponse {
    private String message;     // 메시지 타입 ("BID", "CLOSE", "....")
    private LocalDateTime localDateTime;
    private String nickname;
    private int currentBid;
}
