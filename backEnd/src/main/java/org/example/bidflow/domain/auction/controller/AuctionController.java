package org.example.bidflow.domain.auction.controller;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.WinnerResponseDto;
import org.example.bidflow.domain.auction.service.AuctionService;
import org.example.bidflow.global.dto.RsData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.example.bidflow.domain.auction.dto.AuctionDetailResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

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
}
