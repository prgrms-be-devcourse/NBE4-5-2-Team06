//package org.example.bidflow.global.app;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.bidflow.domain.auction.entity.Auction;
//import org.example.bidflow.domain.auction.repository.AuctionRepository;
//import org.example.bidflow.domain.user.entity.User;
//import org.example.bidflow.domain.user.repository.UserRepository;
//import org.example.bidflow.domain.winner.entity.Winner;
//import org.example.bidflow.domain.winner.repository.WinnerRepository;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class AuctionScheduler {
//    private final RedisCommon redisCommon;
//    private final AuctionRepository auctionRepository;
//    private final WinnerRepository winnerRepository;
//    private final UserRepository userRepository;
//    private final SimpMessagingTemplate simpMessagingTemplate;
//
//    @Scheduled(fixedDelay = 30000)
//    public void auctionSchedule() {
//
//        // 레디스에서 전체 키를 가져오는 메서드
//        Set<String> keys = redisCommon.getAllKeys();
//
//        if(keys.isEmpty()) {
//            log.info("[Scheduler] 현재 진행 중인 경매가 없습니다.");
//            return;
//        }
//
//        for (String key : keys) {
//
//            if(!key.startsWith("auction")) { continue;}
//
//            String id = key.split(":")[1];
//            Long auctionId = Long.valueOf(id);
//            Long ttl = redisCommon.getTTL(key);
//
//            // ttl이 60초 이하로 남았을 때 (경매 종료 직전)
//            if (ttl - 60 < 0) {
//                Integer amount = redisCommon.getFromHash(key, "amount", Integer.class);
//                String userUUID = redisCommon.getFromHash(key, "userUUID", String.class);
//
//                // 입찰자가 없는 경우
//                if (userUUID == null) {
//                    log.warn("[Scheduler] 입찰자 없음, 경매 ID: {}", auctionId);
//                    return;
//                }
//
//                Optional<Auction> auction = auctionRepository.findByAuctionId(auctionId);
//                if(auction.isEmpty()) {
//                    log.warn("[Scheduler] 경매 없음, 경매 ID: {}", auctionId);
//                    continue;
//                }
//
//                Optional<User> userOpt = userRepository.findByUserUUID(userUUID);
//                if(userOpt.isEmpty()){
//                    log.warn("[Scheduler] 사용자 없음, UUID: {}, 경매 ID : {}", userUUID, auctionId);
//                    continue;
//                }
//                User user = userOpt.get();
//
//                // Winner 저장
//                Winner winner = Winner.builder()
//                        .winningBid(amount)
//                        .winTime(auction.get().getEndTime())
//                        .user(user)
//                        .auction(auction.get())
//                        .build();
//
//                winnerRepository.save(winner);
//
//                // 웹소켓 메시지 발송
//                Map<String, Object> message = new HashMap<>();
//                message.put("auctionId", auctionId);
//                message.put("winnerNickname", user.getNickname());
//                message.put("winningBid", amount);
//
//                simpMessagingTemplate.convertAndSend("/sub/auction/" + auctionId, message);
//
//                log.info("[Scheduler] 경매 종료 및 낙찰자 전송 완료 - 경매 ID: {}, 낙찰자: {}, 금액: {}", auctionId, user.getNickname(), amount);
//            }
//
//        }
//    }
//}
