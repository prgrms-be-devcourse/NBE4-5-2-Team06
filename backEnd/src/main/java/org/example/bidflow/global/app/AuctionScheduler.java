package org.example.bidflow.global.app;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.user.entity.User;
import org.example.bidflow.domain.user.repository.UserRepository;
import org.example.bidflow.domain.winner.entity.Winner;
import org.example.bidflow.domain.winner.repository.WinnerRepository;
import org.example.bidflow.domain.winner.service.WinnerService;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuctionScheduler {
    private final RedisCommon redisCommon;
    private final WinnerService winnerService;
    private final AuctionRepository auctionRepository;
    private final WinnerRepository winnerRepository;
    private final UserRepository userRepository;

    @Scheduled(fixedDelay = 60000)
    public void auctionSchedule() {

        // 레디스에서 전체 키를 가져오는 메서드
        Set<String> keys = redisCommon.getAllKeys();

        if(keys.isEmpty()) {
            throw new ServiceException("400", "진행중인 경매가 없습니다.");
        }

        for (String key : keys) {

            if(!key.startsWith("auction")) { continue;}

            String id = key.split(":")[1];
            Long auctionId = Long.valueOf(id);
            Long ttl = redisCommon.getTTL(key);
            // ttl - 여유시간(2분)
            if (ttl - 60 < 0) {
                Integer amount = redisCommon.getFromHash(key, "amount", Integer.class);
                String userUUID = redisCommon.getFromHash(key, "userUUID", String.class);

                if (userUUID != null) {
                    throw new ServiceException("400", "사용자가 일정시간 입찰을 하지않아 입찰 내역이 존재하지 않습니다.(사용자 참가X)");
                }

                Optional<Auction> auction = auctionRepository.findByAuctionId(auctionId);
                LocalDateTime endTime = auction.get().getEndTime();
                User user = userRepository.findByUserUUID(userUUID).get();

                Winner winner = Winner.builder()
                        .winningBid(amount)
                        .winTime(endTime)
                        .user(user)
                        .auction(auction.get())
                        .build();

                winnerRepository.save(winner);
            }

        }
    }
}
