package org.example.bidflow.global.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.bidflow.data.AuctionStatus;
import org.example.bidflow.domain.auction.entity.Auction;
import org.example.bidflow.domain.auction.repository.AuctionRepository;
import org.example.bidflow.domain.product.entity.Product;
import org.example.bidflow.domain.product.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@RequiredArgsConstructor
public class BaseInitData {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    @PostConstruct
    public void init() {
        // 1. 상품 데이터 추가
        Product product1 = Product.builder()
                .productName("Vintage Clock")
                .imageUrl("/static/image/vintage_clock.jpg")
                .build();
        productRepository.save(product1);

        Product product2 = Product.builder()
                .productName("Apple MacBook Pro")
                .imageUrl("/static/image/macbook.jpg")
                .build();
        productRepository.save(product2);

        // 2. 경매 데이터 추가
        Auction auction1 = Auction.builder()
                .product(product1)
                .startPrice(5000)
                .minBid(500)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(7))
                .status(AuctionStatus.ONGOING)
                .build();
        auctionRepository.save(auction1);

        Auction auction2 = Auction.builder()
                .product(product2)
                .startPrice(1000000)
                .minBid(10000)
                .startTime(LocalDateTime.now().plusDays(3))
                .endTime(LocalDateTime.now().plusDays(10))
                .status(AuctionStatus.UPCOMING)
                .build();
        auctionRepository.save(auction2);
    }
}
