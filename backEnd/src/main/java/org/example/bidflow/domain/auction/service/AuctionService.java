package org.example.bidflow.domain.auction.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.domain.auction.dto.AuctionDto;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.bid.service.BidService;
import org.example.bidflow.domain.product.service.ProductService;
import org.example.bidflow.domain.winner.service.WinnerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;


    public List<AuctionDto> getAllAuctions() {
        // AuctionRepository에서 반환한 AuctionDto를 그대로 반환
        return auctionRepository.findAllAuctions();
    }
}
