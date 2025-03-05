package org.example.bidflow.domain.auction.controller;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionResponse;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.service.AuctionService;
import org.example.bidflow.global.dto.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;


    @GetMapping("/auctions")
    public ResponseEntity<RsData<List<AuctionResponse>>> getAllAuctions() {
        // AuctionService에서 AuctionResponse 리스트를 반환
        List<AuctionResponse> response = auctionService.getAllAuctions();
        RsData<List<AuctionResponse>> rsData = new RsData<>("200", "전체 조회가 완료되었습니다.", response);
        return ResponseEntity.ok(rsData);
    }

}
