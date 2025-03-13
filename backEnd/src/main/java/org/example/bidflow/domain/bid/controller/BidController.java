package org.example.bidflow.domain.bid.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bidflow.domain.auction.dto.AuctionBidRequest;
import org.example.bidflow.domain.bid.dto.model.response.BidCreateResponse;
import org.example.bidflow.domain.bid.service.BidService;
import org.example.bidflow.global.utils.JwtProvider;
import org.example.bidflow.domain.bid.dto.model.response.webSocket.WebSocketResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auctions")
public class BidController {

    private final BidService bidService;
    private final JwtProvider jwtProvider;
    private final SimpMessagingTemplate simpMessagingTemplate;

    // 경매 입찰 컨트롤러
    @MessageMapping("/auction/bid")
    public void createBids(@Payload AuctionBidRequest request) {
        String userUUID = jwtProvider.parseUserUUID(request.getToken());
        String nickname = jwtProvider.parseNickname(request.getToken());

        log.info("입찰 요청 수신, userUUID  : {}, nickname: {}", userUUID,  nickname);

        BidCreateResponse response = bidService.createBid(request.getAuctionId(), request);

        // 입찰 성공 시 WebSocket 메시지 보낼 데이터
        WebSocketResponse res = WebSocketResponse.builder()
                .message("입찰 성공")
                .localDateTime(LocalDateTime.now())
                .nickname(response.getNickname())
                .currentBid(request.getAmount())
                .build();

        simpMessagingTemplate.convertAndSend("/sub/auction/" + request.getAuctionId(), res);
        log.info("입찰 브로드캐스트 완료: /sub/auction/{}",  request.getAuctionId());
    }
}
