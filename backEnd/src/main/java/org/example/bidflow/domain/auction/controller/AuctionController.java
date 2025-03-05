package org.example.bidflow.domain.auction.controller;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionDto;
import org.example.bidflow.domain.auction.service.AuctionService;
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
    public List<AuctionDto> getAllAuctions() {
        return auctionService.getAllAuctions();
    }

//    @GetMapping("/admin/auctions")
//    public List<AuctionDto> getAllAuctionsForAdmin() {
//        return auctionService.getAllAuctions();
//    }
}
