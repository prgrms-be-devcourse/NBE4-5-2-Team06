package org.example.bidflow.domain.auction.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.dto.WinnerResponseDto;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.bid.repository.BidRepository;
import org.example.bidflow.domain.winner.entity.Winner;
import org.example.bidflow.domain.winner.repository.WinnerRepository;
import org.example.bidflow.domain.auction.dto.AuctionDetailResponse;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final WinnerRepository winnerRepository;

    // 외부 요청에 대한 거래 종료 기능
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
                .winningBid(/*winningBid.getAmount()*/123123123)
                .winTime(LocalDateTime.now())
                .build();

        // 경매 상태 변경
        /*auction = auction.toBuilder()
                .status(AuctionStatus.FINISHED)
                .winner(winner)
                .build();
        auctionRepository.save(auction);*/
        auction.setStatus(AuctionStatus.FINISHED);
        auction.setWinner(winner);

        return new WinnerResponseDto(winner);
    }

    // 경매 데이터 검증 후 DTO 반환
    @Transactional(readOnly = true)
    public AuctionDetailResponse getAuctionDetail(Long auctionId) {
        Auction auction = auctionRepository.findByAuctionId(auctionId)      // 경매 ID로 경매 객체 존재 여부 확인
                .orElseThrow(() -> new ServiceException("400-1", "해당 경매 상품을 찾을 수 없습니다."));

        if (auction.getStatus() != AuctionStatus.UPCOMING) {
            throw new ServiceException("400-2", "진행 중인 경매가 아닙니다.");
        }

        return AuctionDetailResponse.from(auction); // DTO 변환 후 반환
    }

}
