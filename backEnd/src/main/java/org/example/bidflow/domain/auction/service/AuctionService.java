package org.example.bidflow.domain.auction.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.dto.WinnerResponseDto;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.bid.repository.BidRepository;
import org.example.bidflow.domain.winner.entity.Winner;
import org.example.bidflow.domain.winner.repository.WinnerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final WinnerRepository winnerRepository;

    @Transactional
    public WinnerResponseDto closeAuction(Long auctionId) {
        Auction auction = auctionRepository.findByAuctionIdAndStatus(auctionId, AuctionStatus.ONGOING)
                .orElseThrow(() -> new IllegalArgumentException("진행 중인 경매를 찾을 수 없습니다."));

        // 최고 입찰가 찾기
        Optional<Bid> highestBid = bidRepository.findTopByAuctionOrderByAmountDesc(auction);
        /*
        SELECT * FROM BID_TABLE
        WHERE AUCTION_ID = ?
        ORDER BY AMOUNT DESC
        LIMIT 1;
         */

        if (highestBid.isEmpty()) {
            throw new IllegalArgumentException("입찰 기록이 없는 경매는 종료할 수 없습니다.");
        }

        // 낙찰자 저장 - 전제: 입찰을 할 때, 사용자 정보와 같이 저장된다.
        Bid winningBid = highestBid.get();
        Winner winner = Winner.builder()
                .auction(auction)
                .user(winningBid.getUser()) // 사용자: 낙찰 테이블 -> 사용자 테이블
                .winningBid(winningBid.getAmount())
                .winTime(LocalDateTime.now())
                .build();

        // 경매 상태 변경
        auction = auction.toBuilder()
                .status(AuctionStatus.FINISHED)
                .winner(winner)
                .build();

        return new WinnerResponseDto(winner);
    }
}
