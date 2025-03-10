package org.example.bidflow.domain.auction.controller;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionBidRequest;
import org.example.bidflow.domain.auction.dto.AuctionCheckResponse;
import org.example.bidflow.domain.bid.dto.BidCreateResponse;
import org.example.bidflow.domain.bid.dto.BidDto;
import org.example.bidflow.domain.bid.service.BidService;
import org.example.bidflow.domain.winner.dto.WinnerResponseDto;
import org.example.bidflow.domain.auction.service.AuctionService;
import org.example.bidflow.global.dto.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.bidflow.domain.auction.dto.AuctionDetailResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final BidService bidService;

    @GetMapping
    public ResponseEntity<RsData<List<AuctionCheckResponse>>> getAllAuctions() {
        // AuctionService에서 AuctionResponse 리스트를 반환
        List<AuctionCheckResponse> response = auctionService.getAllAuctions();
        RsData<List<AuctionCheckResponse>> rsData = new RsData<>("200", "전체 조회가 완료되었습니다.", response);
        return ResponseEntity.ok(rsData);
    }

    @PostMapping("/{auctionId}/close")
    public ResponseEntity<RsData<WinnerResponseDto>> closeAuction(@PathVariable Long auctionId) {
        WinnerResponseDto winner = auctionService.closeAuction(auctionId);

        RsData<WinnerResponseDto> response = new RsData<>("200-SUCCESS", "경매가 성공적으로 종료되었습니다.", winner);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 특정 경매 상세 조회 컨트롤러
    @GetMapping("/{auctionId}")
    public ResponseEntity<RsData<AuctionDetailResponse>> getAuctionDetail(@PathVariable("auctionId") Long auctionId) {
        AuctionDetailResponse response = auctionService.getAuctionDetail(auctionId);
        RsData<AuctionDetailResponse> rsData = new RsData<>("200", "경매가 성공적으로 조회되었습니다.", response);
        return ResponseEntity.ok(rsData);
    }

    // 경매 입찰 컨트롤러
    @PostMapping("/{auctionId}/bids")
    public ResponseEntity<RsData<BidCreateResponse>> createBids(@PathVariable Long auctionId, @RequestBody AuctionBidRequest request) {
        BidCreateResponse response = bidService.createBid(auctionId, request);
        RsData<BidCreateResponse> rsData = new RsData<>("200", "입찰이 성공적으로 등록돠었습니다.", response);
        return ResponseEntity.ok(rsData);
    }
}
