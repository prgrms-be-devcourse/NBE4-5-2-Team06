package org.example.bidflow.global.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

// 경매 종료 -> 종료 상태 변경 및 이벤트 발행
@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionSchedulerEvent {
    private final RedisTemplate<String, String> redisTemplate;
    private final AuctionRepository auctionRepository;
    private final ApplicationEventPublisher eventPublisher;
// @Scheduled(fixedDelay = 30000) -> 약 30초
// @Scheduled(fixedDelay = 10000) -> 5초
// @Scheduled(fixedDelay = 1000) -> 아예 실행 X. 로그도 안뜨는거같던데

    @Transactional
    @Scheduled(fixedDelay = 3000) // 3초마다 실행 -> 경매 종료 시 바로 실행
    public void processAuctions() {
        Set<String> keys = redisTemplate.keys("auction:*");

        if (keys.isEmpty()) {
            log.info("[Scheduler] 현재 진행 중인 경매가 없습니다.");
            return;
        }

        for (String key : keys) {
            Long auctionId = Long.valueOf(key.split(":")[1]);
            Optional<Auction> auctionOpt = auctionRepository.findById(auctionId);
            if (auctionOpt.isEmpty()) continue;

            Auction auction = auctionOpt.get();

            // 경매 종료 시간 도달 && 아직 종료 처리 안 됐을 때
            if (LocalDateTime.now().isAfter(auction.getEndTime()) && auction.getStatus() != AuctionStatus.FINISHED) {
                auction.setStatus(AuctionStatus.FINISHED);
                auctionRepository.save(auction);
                log.info("[Scheduler] 경매 종료 처리 - 경매 ID: {}", auctionId);

                // 즉시 이벤트 발행
                eventPublisher.publishEvent(new AuctionFinishedEvent(this, auction));
            }
        }
    }
}//