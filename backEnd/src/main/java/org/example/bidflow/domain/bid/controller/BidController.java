package org.example.bidflow.domain.bid.controller;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionBidRequest;
import org.example.bidflow.domain.bid.dto.model.response.BidCreateResponse;
import org.example.bidflow.domain.bid.service.BidService;
import org.example.bidflow.global.dto.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auctions")
public class BidController {

    private final BidService bidService;
    // 경매 입찰 컨트롤러
    @PostMapping("/{auctionId}/bids")
    public ResponseEntity<RsData<BidCreateResponse>> createBids(@PathVariable Long auctionId, @RequestBody AuctionBidRequest request) {
        BidCreateResponse response = bidService.createBid(auctionId, request);
        RsData<BidCreateResponse> rsData = new RsData<>("200", "입찰이 성공적으로 등록돠었습니다.", response);
        return ResponseEntity.ok(rsData);
    }

}
