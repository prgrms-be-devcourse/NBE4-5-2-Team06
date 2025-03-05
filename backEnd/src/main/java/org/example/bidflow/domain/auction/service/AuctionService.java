package org.example.bidflow.domain.auction.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.*;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.product.entity.Product;
import org.example.bidflow.domain.product.repository.ProductRepository;
import org.example.bidflow.global.dto.RsData;
import org.example.bidflow.domain.winner.dto.WinnerResponseDto;
import org.example.bidflow.domain.bid.entity.Bid;
import org.example.bidflow.domain.bid.repository.BidRepository;
import org.example.bidflow.domain.winner.entity.Winner;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;

    public List<AuctionCheckResponse> getAllAuctions()  {
        
      // 경매 목록 조회
        List<Auction> auctions = auctionRepository.findAllAuctions();

        return auctions.stream()
                .map(auction -> AuctionCheckResponse.builder()
                        .auctionId(auction.getAuctionId())
                        .productName(auction.getProduct().getProductName())  // Product에서 상품명 가져오기
                        .imageUrl(auction.getProduct().getImageUrl())      // Product에서 이미지 URL 가져오기
                        .currentPrice(auction.getStartPrice())  // 현재 가격 가져오기
                        .status(auction.getStatus().toString())  // Enum을 String으로 변환하기
                        .startTime(auction.getStartTime())
                        .endTime(auction.getEndTime())
                        .build())
                .collect(Collectors.toList());

    }

    // 경매 등록 서비스
    @Transactional
    public RsData<AuctionCreateResponse> createAuction(AuctionRequest requestDto) {

        // 경매 종료 시간이 시작 시간보다 빠르면 예외 처리
        if (requestDto.getStartTime().isAfter(requestDto.getEndTime())) {
            throw new ServiceException("400", "경매 종료 시간이 시작 시간보다 빠를 수 없습니다.");
        }

        // 최소 등록 시간 검증 (현재 시간 기준 최소 2일 전)
        LocalDateTime now = LocalDateTime.now();
        if (requestDto.getStartTime().isBefore(now.plusDays(2))) {
            throw new ServiceException("404", "상품 등록 시간은 최소 2일 전부터 가능합니다.");
        }

        // 상품 정보 저장
        Product product = Product.builder()
                .productName(requestDto.getProductName())
                .imageUrl(requestDto.getImageUrl())
                .description(requestDto.getDescription())
                .build();
        productRepository.save(product);

        // 경매 정보 저장
        Auction auction = Auction.builder()
                .product(product)
                .startPrice(requestDto.getStartPrice())
                .minBid(requestDto.getMinBid())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .status(AuctionStatus.UPCOMING)
                .build();
        auctionRepository.save(auction);

        // 성공 응답 반환
        return new RsData<>("201", "경매가 등록되었습니다.", AuctionCreateResponse.from(auction));
    }

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
    @Transactional
    public AuctionDetailResponse getAuctionDetail(Long auctionId) {
        Auction auction = auctionRepository.findByAuctionId(auctionId)      // 경매 ID로 경매 객체 존재 여부 확인
                .orElseThrow(() -> new ServiceException("400-1", "해당 경매 상품을 찾을 수 없습니다."));

        if (auction.getStatus() != AuctionStatus.ONGOING) {
            throw new ServiceException("400-2", "진행 중인 경매가 아닙니다.");
        }

        return AuctionDetailResponse.from(auction); // DTO 변환 후 반환
    }

}