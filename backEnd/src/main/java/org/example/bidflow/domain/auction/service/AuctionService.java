package org.example.bidflow.domain.auction.service;

import lombok.RequiredArgsConstructor;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.dto.AuctionRequest;
import org.example.bidflow.domain.auction.dto.AuctionResponse;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.product.entity.Product;
import org.example.bidflow.domain.product.repository.ProductRepository;
import org.example.bidflow.global.dto.RsData;
import org.example.bidflow.global.exception.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    @Transactional
    public RsData<AuctionResponse> createAuction(AuctionRequest requestDto) {
        if (requestDto.getStartTime().isAfter(requestDto.getEndTime())) {
            throw new ServiceException("400", "경매 종료 시간이 시작 시간보다 빠를 수 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (requestDto.getStartTime().isBefore(now.plusDays(2))) {
            throw new ServiceException("404", "상품 등록 시간은 최소 2일 전부터 가능합니다.");
        }

        Optional<Product> existingProduct = productRepository.findByProductName(requestDto.getProductName());
        if (existingProduct.isPresent()) {
            throw new ServiceException("400", "이미 등록된 상품입니다.");
        }

        Product product = Product.builder()
                .productName(requestDto.getProductName())
                .imageUrl(requestDto.getImageUrl())
                .description(requestDto.getDescription())
                .build();
        productRepository.save(product);

        Auction auction = Auction.builder()
                .product(product)
                .startPrice(requestDto.getStartPrice())
                .minBid(requestDto.getMinBid())
                .startTime(requestDto.getStartTime())
                .endTime(requestDto.getEndTime())
                .status(AuctionStatus.UPCOMING)
                .build();
        auctionRepository.save(auction);

        return new RsData<>("201", "경매가 등록되었습니다.", AuctionResponse.of(auction, product));
    }
}
