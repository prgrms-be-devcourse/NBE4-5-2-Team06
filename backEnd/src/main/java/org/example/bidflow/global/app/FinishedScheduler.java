//package org.example.bidflow.global.app;
//
//import lombok.RequiredArgsConstructor;
//import org.example.bidflow.data.AuctionStatus;
//import org.example.bidflow.domain.auction.entity.Auction;
//import org.example.bidflow.domain.auction.repository.AuctionRepository;
//import org.example.bidflow.global.exception.ServiceException;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.Set;
//
//@Service
//@RequiredArgsConstructor
//public class FinishedScheduler {
//    private final RedisCommon redisCommon;
//    private final AuctionRepository auctionRepository;
//
//    @Scheduled(fixedDelay = 30000)
//    public void finishedSchedule() {
//
//        Set<String> keys = redisCommon.getAllKeys();
//
//        if(keys.isEmpty()) {
//            throw new ServiceException("400", "진행중인 경매가 없습니다.");
//        }
//
//        for (String key : keys) {
//
//            if (!key.startsWith("auction")) { continue;}
//
//            String id = key.split(":")[1];
//            Long auctionId = Long.valueOf(id);
//            Auction auction = auctionRepository.findById(auctionId).orElse(null);
//            if (auction == null) {
//                throw new ServiceException("400", "경매가 존재하지 않습니다.");
//            }
//
//            LocalDateTime endTime = auction.getEndTime();
//
//            if (LocalDateTime.now().isAfter(endTime)) {
//                auction.setStatus(AuctionStatus.FINISHED);
//                auctionRepository.save(auction);
//            }
//        }
//    }
//}
