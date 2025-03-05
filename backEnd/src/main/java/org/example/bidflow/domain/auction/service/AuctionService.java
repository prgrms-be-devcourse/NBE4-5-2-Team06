package org.example.bidflow.domain.auction.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionResponse;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;

//    public RsData<List<Auction>> getAllAuctions() {
//        // AuctionRepository에서 반환한 AuctionDto를 그대로 반환
//        return auctionRepository.findAllAuctions();
//    }

    public List<AuctionResponse> getAllAuctions()  {
        // 경매 목록 조회
        List<Auction> auctions = auctionRepository.findAllAuctions();

        return auctions.stream()
                .map(auction -> AuctionResponse.builder()
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
}