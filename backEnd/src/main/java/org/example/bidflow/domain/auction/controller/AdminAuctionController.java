package org.example.bidflow.domain.auction.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionRequest;
import org.example.bidflow.domain.auction.dto.AuctionResponse;
import org.example.bidflow.domain.auction.service.AuctionService;
import org.example.bidflow.global.dto.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/auctions")
@RequiredArgsConstructor
public class AdminAuctionController {
    private final AuctionService auctionService;

    @PostMapping
    public ResponseEntity<RsData<AuctionResponse>> createAuction(@Valid @RequestBody AuctionRequest requestDto) {
        RsData<AuctionResponse> response = auctionService.createAuction(requestDto);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
