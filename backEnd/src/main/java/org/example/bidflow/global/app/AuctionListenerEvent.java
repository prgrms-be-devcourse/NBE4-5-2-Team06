package org.example.bidflow.global.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.user.entity.User;
import org.example.bidflow.domain.user.repository.UserRepository;
import org.example.bidflow.domain.winner.entity.Winner;
import org.example.bidflow.domain.winner.repository.WinnerRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


// 이벤트 수신 및 낙찰자 처리 후 WebSocket 메시지 전송
@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionListenerEvent {
    private final WinnerRepository winnerRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RedisCommon redisCommon;

    // 이벤트 발생 시 즉시 실행
    @EventListener
    public void handleAuctionFinished(AuctionFinishedEvent event) {
        Auction auction = event.getAuction();
        Long auctionId= auction.getAuctionId();
        String key = "auction:" + auction.getAuctionId();

        // 입찰 정보 확인
        Integer amount = redisCommon.getFromHash(key, "amount", Integer.class);
        String userUUID = redisCommon.getFromHash(key, "userUUID", String.class);

        if (amount == null) {
            log.warn("[Scheduler] 입찰 금액 없음, 경매 ID: {}", auctionId);
            return;
        }

        if (userUUID == null) {
            log.warn("[AuctionEvent] 입찰자 없음 - 경매 ID: {}", auctionId);
            return;
        }

        Optional<User> userOpt = userRepository.findByUserUUID(userUUID);
        if (userOpt.isEmpty()) {
            log.warn("[AuctionEvent] 사용자 없음 - UUID: {}, 경매 ID: {}", userUUID, auctionId);
            return;
        }
        User user = userOpt.get();

        // 낙찰자 저장
        Winner winner = Winner.builder()
                .winningBid(amount)
                .winTime(auction.getEndTime())
                .user(user)
                .auction(auction)
                .build();
        winnerRepository.save(winner);
        log.info("[AuctionEvent] 낙찰자 저장 - 경매 ID: {}, 금액: {}", auctionId, amount);

        // WebSocket 메시지 전송
        Map<String, Object> message = new HashMap<>();
        message.put("auctionId", auctionId);
        message.put("winnerNickname", user.getNickname());
        message.put("winningBid", amount);
        simpMessagingTemplate.convertAndSend("/sub/auction/" + auctionId, message);
        log.info("[AuctionEvent] WebSocket 전송 완료 - 경매 ID: {}", auctionId);
    }
}
